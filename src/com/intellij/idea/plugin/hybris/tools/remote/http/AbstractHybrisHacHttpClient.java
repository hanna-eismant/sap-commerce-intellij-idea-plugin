package com.intellij.idea.plugin.hybris.tools.remote.http;

import com.intellij.idea.plugin.hybris.common.services.CommonIdeaService;
import com.intellij.idea.plugin.hybris.settings.HybrisDeveloperSpecificProjectSettingsComponent;
import com.intellij.idea.plugin.hybris.settings.HybrisRemoteConnectionSettings;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.ConnectException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_MOVED_TEMP;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.apache.http.HttpHeaders.USER_AGENT;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_MOVED_TEMPORARILY;
import static org.apache.http.HttpStatus.SC_SERVICE_UNAVAILABLE;
import static org.apache.http.HttpVersion.HTTP_1_1;

public abstract class AbstractHybrisHacHttpClient {

    private static final Logger LOG = Logger.getInstance(AbstractHybrisHacHttpClient.class);
    public static final int DEFAULT_HAC_TIMEOUT = 6000;
    protected String sessionId;

    public String login(Project project) {
        final var settings = HybrisDeveloperSpecificProjectSettingsComponent.getInstance(project).getActiveHacRemoteConnectionSettings(project);
        return login(project, settings);
    }

    public String login(@NotNull final Project project, @NotNull final HybrisRemoteConnectionSettings settings) {
        final String hostHacURL = getHostHacURL(project, settings);
        sessionId = getSessionId(hostHacURL, project, settings);
        if (sessionId == null) {
            return "Unable to obtain sessionId for "+hostHacURL;
        }
        final String csrfToken = getCsrfToken(hostHacURL, sessionId, project, settings);
        final List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("j_username", settings.getHacLogin()));
        params.add(new BasicNameValuePair("j_password", settings.getHacPassword()));
        params.add(new BasicNameValuePair("_csrf", csrfToken));
        final String loginURL = hostHacURL + "/j_spring_security_check";
        final HttpResponse response = post(project, loginURL, params, false, DEFAULT_HAC_TIMEOUT, settings);
        if (response.getStatusLine().getStatusCode() == SC_MOVED_TEMPORARILY) {
            final Header location = response.getFirstHeader("Location");
            if (location != null && location.getValue().contains("login_error")) {
                return "Wrong username/password. Set your credentials in [y] tool window.";
            }
        }
        sessionId = CookieParser.getInstance().getSpecialCookie(response.getAllHeaders());
        if (sessionId != null) {
            return StringUtils.EMPTY;
        }
        final int statusCode = response.getStatusLine().getStatusCode();
        final StringBuilder sb = new StringBuilder();
        sb.append("HTTP ");
        sb.append(statusCode);
        sb.append(' ');
        switch (statusCode) {
            case HTTP_OK -> sb.append("Unable to obtain sessionId from response");
            case HTTP_MOVED_TEMP -> sb.append(response.getFirstHeader("Location"));
            default -> sb.append(response.getStatusLine().getReasonPhrase());
        }
        return sb.toString();
    }

    @NotNull
    public final HttpResponse post(
        @NotNull Project project,
        @NotNull String actionUrl,
        @NotNull List<BasicNameValuePair> params,
        boolean canReLoginIfNeeded,
        final long timeout
    ) {
        return post(project, actionUrl, params, canReLoginIfNeeded, timeout, null);
    }

    @NotNull
    public final HttpResponse post(
        @NotNull Project project,
        @NotNull String actionUrl,
        @NotNull List<BasicNameValuePair> params,
        boolean canReLoginIfNeeded
    ) {
        return post(project, actionUrl, params, canReLoginIfNeeded, DEFAULT_HAC_TIMEOUT, null);
    }

    @NotNull
    public final HttpResponse post(
        @NotNull Project project,
        @NotNull String actionUrl,
        @NotNull List<BasicNameValuePair> params,
        boolean canReLoginIfNeeded,
        final long timeout,
        HybrisRemoteConnectionSettings settings
    ) {
        if (sessionId == null) {
            final String errorMessage = login(project);
            if (StringUtils.isNotBlank(errorMessage)) {
                return createErrorResponse(errorMessage);
            }
        }
        final String csrfToken = getCsrfToken(getHostHacURL(project), sessionId, project, settings);
        if (csrfToken == null) {
            this.sessionId = null;
            if (canReLoginIfNeeded) {
                return post(project, actionUrl, params, false, timeout, settings);
            }
            return createErrorResponse("Unable to obtain csrfToken for sessionId="+sessionId);
        }
        final HttpClient client = createAllowAllClient(timeout);
        if (client == null) {
            return createErrorResponse("Unable to create HttpClient");
        }
        final HttpPost post = new HttpPost(actionUrl);
        post.setHeader("User-Agent", USER_AGENT);
        post.setHeader("X-CSRF-TOKEN", csrfToken);
        post.setHeader("Cookie", "JSESSIONID=" + sessionId);

        HttpResponse response;
        try {
            post.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
            response = client.execute(post);
        } catch (IOException e) {
            LOG.warn(e.getMessage(), e);
            return createErrorResponse(e.getMessage());
        }

        boolean needsLogin = response.getStatusLine().getStatusCode() == SC_FORBIDDEN;
        if (response.getStatusLine().getStatusCode() == SC_MOVED_TEMPORARILY) {
            Header location = response.getFirstHeader("Location");
            if (location != null && location.getValue().contains("login")) {
                needsLogin = true;
            }
        }

        if (needsLogin) {
            this.sessionId = null;
            if (canReLoginIfNeeded) {
                return post(project, actionUrl, params, false);
            }
        }
        return response;
    }

    protected HttpResponse createErrorResponse(final String reasonPhrase) {
        return new BasicHttpResponse(new BasicStatusLine(HTTP_1_1, SC_SERVICE_UNAVAILABLE, reasonPhrase));
    }

    public String getHostHacURL(final Project project) {
        return CommonIdeaService.getInstance().getActiveHacUrl(project);
    }

    public String getSslProtocol(
        final Project project,
        final @Nullable HybrisRemoteConnectionSettings settings
    ) {
        return CommonIdeaService.getInstance().getActiveSslProtocol(project, settings);
    }

    public String getHostHacURL(final Project project, final HybrisRemoteConnectionSettings settings) {
        return CommonIdeaService.getInstance().getHostHacUrl(project, settings);
    }

    protected CloseableHttpClient createAllowAllClient(final long timeout) {
        SSLContext sslcontext = null;
        try {
            sslcontext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {

                @Override
                public boolean isTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
                    return true;
                }
            }).build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            LOG.warn(e.getMessage(), e);
            return null;
        }
        SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(sslcontext, NoopHostnameVerifier.INSTANCE);

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.getSocketFactory())
            .register("https", sslConnectionFactory)
            .build();

        HttpClientConnectionManager ccm = new BasicHttpClientConnectionManager(registry);
        HttpClientBuilder builder = HttpClients.custom();
        builder.setConnectionManager(ccm);
        RequestConfig config = RequestConfig.custom()
                                            .setSocketTimeout((int) timeout)
                                            .setConnectTimeout((int) timeout)
                                            .build();
        builder.setDefaultRequestConfig(config);
        return builder.build();
    }


    protected String getSessionId(
        final String hacURL,
        final @NotNull Project project,
        final @NotNull HybrisRemoteConnectionSettings settings
    ) {
        final Response res = getResponseForUrl(hacURL, project, settings);
        if (res == null) {
            return null;
        }
        return res.cookie("JSESSIONID");
    }

    protected Response getResponseForUrl(
        final String hacURL,
        final @NotNull Project project,
        final @NotNull HybrisRemoteConnectionSettings settings
    ) {
        try {
            final String sslProtocol = getSslProtocol(project, settings);
            return connect(hacURL, sslProtocol).method(Method.GET).execute();
        } catch (ConnectException ce) {
            return null;
        } catch (NoSuchAlgorithmException | IOException | KeyManagementException e) {
            LOG.warn(e.getMessage(), e);
            return null;
        }
    }

    protected String getCsrfToken(
        final @NotNull String hacURL,
        final @NotNull String sessionId,
        final @NotNull Project project,
        final @Nullable HybrisRemoteConnectionSettings settings
    ) {
        try {
            final String sslProtocol = getSslProtocol(project, settings);

            final Document doc = connect(hacURL, sslProtocol).cookie("JSESSIONID", sessionId).get();
            final Elements csrfMetaElt = doc.select("meta[name=_csrf]");
            return csrfMetaElt.attr("content");
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
            LOG.warn(e.getMessage(), e);
        }
        return null;
    }

    private Connection connect(@NotNull String url, final String sslProtocol) throws
                                                                              NoSuchAlgorithmException,
                                                                              KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

            @Nullable
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(@NotNull X509Certificate[] certs, @NotNull String authType) {
            }

            public void checkServerTrusted(@NotNull X509Certificate[] certs, @NotNull String authType) {
            }
        }};

        SSLContext sc = SSLContext.getInstance(sslProtocol);
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(new NoopHostnameVerifier());
        return Jsoup.connect(url);
    }
}
