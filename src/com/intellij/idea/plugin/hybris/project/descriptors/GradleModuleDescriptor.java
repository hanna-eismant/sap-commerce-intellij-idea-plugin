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

package com.intellij.idea.plugin.hybris.project.descriptors;

import com.intellij.idea.plugin.hybris.common.HybrisConstants;
import com.intellij.idea.plugin.hybris.project.exceptions.HybrisConfigurationException;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class GradleModuleDescriptor extends RootModuleDescriptor {

    public GradleModuleDescriptor(
        @NotNull final File moduleRootDirectory,
        @NotNull final HybrisProjectDescriptor rootProjectDescriptor
    ) throws HybrisConfigurationException {
        super(moduleRootDirectory, rootProjectDescriptor, moduleRootDirectory.getName());
    }

    @NotNull
    @Override
    public HybrisModuleDescriptorType getDescriptorType() {
        return HybrisModuleDescriptorType.GRADLE;
    }

    public File getGradleFile() {
        return new File(moduleRootDirectory, HybrisConstants.BUILD_GRADLE);
    }
}
