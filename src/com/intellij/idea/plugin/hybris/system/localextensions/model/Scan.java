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

// Generated on Thu Jan 12 19:15:30 CET 2023
// DTD/Schema  :    null

package com.intellij.idea.plugin.hybris.system.localextensions.model;

import com.intellij.idea.plugin.hybris.util.xml.FalseAttributeValue;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;
import org.jetbrains.annotations.NotNull;

/**
 * null:scanType interface.
 * <pre>
 * <h3>Type null:scanType documentation</h3>
 * Scans for extensions in a given folder.
 * </pre>
 */
public interface Scan extends DomElement {

	/**
	 * Returns the value of the dir child.
	 * <pre>
	 * <h3>Attribute null:dir documentation</h3>
	 * Path to the extension folder relative to the platform home.
	 * </pre>
	 * @return the value of the dir child.
	 */
	@NotNull
	@Attribute ("dir")
	@Required
	GenericAttributeValue<String> getDir();


	/**
	 * Returns the value of the autoload child.
	 * <pre>
	 * <h3>Attribute null:autoload documentation</h3>
	 * Defines which extensions shall be loaded. Can be 'true' for loading all that can be found inside that folder or 'false' to load only extensions that are required by others.
	 * </pre>
	 * @return the value of the autoload child.
	 */
	@NotNull
	@Attribute ("autoload")
	FalseAttributeValue getAutoload();


	/**
	 * Returns the value of the depth child.
	 * <pre>
	 * <h3>Attribute null:depth documentation</h3>
	 * The maximum directory depth to traverse when scanning for extensions.
	 * </pre>
	 * @return the value of the depth child.
	 */
	@NotNull
	@Attribute ("depth")
	GenericAttributeValue<Integer> getDepth();


}
