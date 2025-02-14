/*
 * This file is part of "hybris integration" plugin for Intellij IDEA.
 * Copyright (C) 2014-2016 Alexander Bartash <AlexanderBartash@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.intellij.idea.plugin.hybris.impex.psi.references;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.idea.plugin.hybris.impex.psi.ImpexFullHeaderParameter;
import com.intellij.idea.plugin.hybris.impex.psi.ImpexHeaderLine;
import com.intellij.idea.plugin.hybris.impex.psi.ImpexHeaderTypeName;
import com.intellij.idea.plugin.hybris.psi.util.PsiUtils;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;

/**
 * Created by Martin Zdarsky-Jones (martin.zdarsky@hybris.com) on 15/06/2016.
 */
public abstract class ImpexHeaderTypeNameMixin extends ASTWrapperPsiElement implements ImpexHeaderTypeName {

    @Serial
    private static final long serialVersionUID = -4201751443049498642L;
    private ImpexTSItemReference myReference;

    public ImpexHeaderTypeNameMixin(@NotNull final ASTNode astNode) {
        super(astNode);
    }

    @Override
    public void subtreeChanged() {
        putUserData(ImpexTSItemReference.getCACHE_KEY(), null);

        final var header = PsiTreeUtil.getParentOfType(this, ImpexHeaderLine.class);
        if (header != null) {
            header.getFullHeaderParameterList().stream()
                  .map(ImpexFullHeaderParameter::getAnyHeaderParameterName)
                  .forEach(it -> it.putUserData(ImpexTSAttributeReference.getCACHE_KEY(), null));
        }
    }

    @NotNull
    @Override
    public final PsiReference[] getReferences() {
        if (PsiUtils.shouldCreateNewReference(myReference, getText())) {
            myReference = new ImpexTSItemReference(this);
        }
        return new PsiReference[]{myReference};
    }

    @Override
    protected Object clone() {
        final ImpexHeaderTypeNameMixin result = (ImpexHeaderTypeNameMixin) super.clone();
        result.myReference = null;
        return result;
    }
}
