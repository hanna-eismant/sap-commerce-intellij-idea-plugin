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

package com.intellij.idea.plugin.hybris.codeInspection.rule.typeSystem

import com.intellij.idea.plugin.hybris.codeInspection.fix.XmlDeleteAttributeQuickFix
import com.intellij.idea.plugin.hybris.system.type.model.Cardinality
import com.intellij.idea.plugin.hybris.system.type.model.Items
import com.intellij.idea.plugin.hybris.system.type.model.RelationElement
import com.intellij.idea.plugin.hybris.system.type.model.elements
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.project.Project
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder
import com.intellij.util.xml.highlighting.DomHighlightingHelper

class TSOrderingOfRelationShouldBeAvoided : AbstractTSInspection() {

    override fun inspect(
        project: Project,
        dom: Items,
        holder: DomElementAnnotationHolder,
        helper: DomHighlightingHelper,
        severity: HighlightSeverity
    ) {
        dom.relations.elements.forEach { check(it, holder, severity) }
    }

    private fun check(
        relation: RelationElement,
        holder: DomElementAnnotationHolder,
        severity: HighlightSeverity
    ) {
        val cardinality = relation.cardinality.value
        val ordered = relation.ordered.value
        if (relation.xmlElement != null && relation.ordered.xmlElement != null && cardinality == Cardinality.MANY && ordered) {
            holder.createProblem(
                relation.ordered,
                severity,
                displayName,
                XmlDeleteAttributeQuickFix(RelationElement.ORDERED)
            )
        }
    }
}