/*
 * This file is part of "SAP Commerce Developers Toolset" plugin for Intellij IDEA.
 * Copyright (C) 2019 EPAM Systems <hybrisideaplugin@epam.com>
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

// Generated on Mon Jan 09 16:31:21 CET 2023
// DTD/Schema  :    null
package com.intellij.idea.plugin.hybris.system.extensioninfo.model;

import com.intellij.util.xml.*;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;

/**
 * null:hmcmoduleType interface.
 * <pre>
 * <h3>Type null:hmcmoduleType documentation</h3>
 * Configures an hmc module for the extension. Required directory: /hmc.
 * </pre>
 */
public interface HmcModule extends DomElement {

	/**
	 * Returns the value of the additionalclasspath child.
	 * <pre>
	 * <h3>Attribute null:additionalclasspath documentation</h3>
	 * Deprecated. Not used anymore.
	 * </pre>
	 * @return the value of the additionalclasspath child.
	 */
	@NotNull
	@Attribute ("additionalclasspath")
	GenericAttributeValue<String> getAdditionalClasspath();


	/**
	 * Returns the value of the extensionclassname child.
	 * <pre>
	 * <h3>Attribute null:extensionclassname documentation</h3>
	 * Name of the extension's HMCExtension class.
	 * </pre>
	 * @return the value of the extensionclassname child.
	 */
	@NotNull
	@Attribute ("extensionclassname")
	GenericAttributeValue<String> getExtensionClassname();


	/**
	 * Returns the value of the sourceavailable child.
	 * <pre>
	 * <h3>Attribute null:sourceavailable documentation</h3>
	 * Deprecated. Has no effect and will be evaluated always to 'true' if a 'hmc/src' directory is available
	 * </pre>
	 * @return the value of the sourceavailable child.
	 */
	@NotNull
	@Attribute ("sourceavailable")
	GenericAttributeValue<Boolean> getSourceAvailable();


}
