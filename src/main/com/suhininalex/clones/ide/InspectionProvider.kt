package com.suhininalex.clones.ide

import com.intellij.codeInspection.*
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.suhininalex.clones.core.cloneManager
import com.suhininalex.clones.core.languagescope.LanguageIndexedPsiManager
import com.suhininalex.clones.core.postprocessing.*
import com.suhininalex.clones.core.structures.Clone
import com.suhininalex.clones.core.structures.CloneClass
import com.suhininalex.clones.core.structures.IndexedSequence
import java.awt.EventQueue
import com.suhininalex.clones.core.utils.*
import com.suhininalex.clones.ide.configuration.PluginLabels
import com.suhininalex.clones.ide.toolwindow.CloneToolwindowManager

class InspectionProvider : LocalInspectionTool() {

    override fun getGroupDisplayName() = PluginLabels.getLabel("inspection-group-display-name")

    override fun getShortName() = "CloneDetection"

    override fun getDisplayName() = PluginLabels.getLabel("inspection-display-name")

    override fun isInitialized(): Boolean =
            CurrentProject?.cloneManager?.initialized ?: false

    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor> {
        val indexedPsiDefiner = LanguageIndexedPsiManager.getIndexedPsiDefiner(file) ?: return emptyArray()
        val inspections = indexedPsiDefiner.getIndexedChildren(file).flatMap { element ->
            getInspectionsFromIndexedElement(manager, element, indexedPsiDefiner.createIndexedSequence(element))
        }
        return inspections.toTypedArray()
    }

    private fun getInspectionsFromIndexedElement(manager: InspectionManager, element: PsiElement, sequence: IndexedSequence): List<ProblemDescriptor> {
        try {
            val cloneManager = element.project.cloneManager.instance
            return cloneManager.getSequenceFilteredClones(sequence)
                .flatMap { cloneClass ->
                    cloneClass.clones
                            .filter { it.firstPsi in element }
                            .map { clone -> manager.createProblemDescriptor(element, cloneClass, clone) }
                            .toList()
                }
        } catch (e: PsiInvalidElementAccessException){
            // just drop it if clone range already is invalid
            return emptyList()
        }
    }

    private fun InspectionManager.createProblemDescriptor(element: PsiElement, cloneClass: CloneClass, clone: Clone): ProblemDescriptor =
        createProblemDescriptor(
            element,
            clone.getTextRangeInIndexedFragment(),
            PluginLabels.getLabel("inspection-problem-description"),
            ProblemHighlightType.WEAK_WARNING,
            true, //is on the fly
            CloneReport(cloneClass, clone)
        )

}

operator fun PsiElement.contains(element: PsiElement): Boolean =
    containingFile == element.containingFile && element.textRange in textRange

class CloneReport(val cloneClass: CloneClass, val clone: Clone) : LocalQuickFix {

    override fun getName(): String =
        PluginLabels.getLabel("inspection-fix-label")
                .replace("\$startLine","${clone.firstPsi.startLine}")
                .replace("\$endLine","${clone.lastPsi.endLine}")

    override fun getFamilyName() = PluginLabels.getLabel("inspection-fix-family-name")

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        EventQueue.invokeLater { CloneToolwindowManager.showClonesData(cloneClass) }
    }
}