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
package com.intellij.idea.plugin.hybris.view

import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.ProjectViewNodeDecorator
import com.intellij.ide.projectView.impl.nodes.ProjectViewModuleGroupNode
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.intellij.idea.plugin.hybris.common.utils.HybrisIcons
import com.intellij.idea.plugin.hybris.project.descriptors.HybrisModuleDescriptorType
import com.intellij.idea.plugin.hybris.settings.HybrisProjectSettingsComponent
import com.intellij.openapi.roots.ProjectRootManager

class HybrisProjectViewNodeDecorator : ProjectViewNodeDecorator {

    override fun decorate(node: ProjectViewNode<*>?, data: PresentationData?) {
        if (data == null) return
        when (node) {
            is PsiDirectoryNode -> decorateModule(node, data)
        }
    }

    private fun decorateModule(node: PsiDirectoryNode, data: PresentationData) {
        val vf = node.virtualFile ?: return
        if (node.parent !is ProjectViewModuleGroupNode || node.parent == null) return
        val module = ProjectRootManager.getInstance(node.project).fileIndex.getModuleForFile(vf) ?: return
        val descriptorType = HybrisProjectSettingsComponent.getInstance(module.project).getModuleSettings(module).descriptorType

        when (descriptorType) {
            HybrisModuleDescriptorType.CCV2 -> data.setIcon(HybrisIcons.MODULE_CCV2_GROUP)
            HybrisModuleDescriptorType.CONFIG -> data.setIcon(AllIcons.Nodes.ConfigFolder)
            else -> {}
        }
    }
}
