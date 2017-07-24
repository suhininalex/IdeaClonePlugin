package com.suhininalex.clones.ide

import com.intellij.codeInspection.*
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.suhininalex.clones.core.CloneIndexer
import com.suhininalex.clones.core.postprocessing.getFileFilteredClones
import com.suhininalex.clones.core.structures.Clone
import com.suhininalex.clones.core.structures.CloneClass
import java.awt.EventQueue
import com.suhininalex.clones.core.utils.*
import com.suhininalex.clones.ide.configuration.PluginLabels
import com.suhininalex.clones.ide.toolwindow.CloneToolwindowManager

class InspectionProvider : LocalInspectionTool() {

    override fun getGroupDisplayName() = PluginLabels.getMessage("inspection-group-display-name")

    override fun getShortName() = "CloneDetection"

    override fun getDisplayName() = PluginLabels.getMessage("inspection-display-name")

    override fun getStaticDescription(): String = PluginLabels.getMessage("inspection-description")

    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor> {
        Logger.log("[Inspection] Processing file ${file.name}")
        CloneFinderIndex.enshureUpToDate(file.project)
        try {
            return CloneIndexer.getFileFilteredClones(file.virtualFile).flatMap { cloneClass ->
                    cloneClass.clones
                            .filter { it.firstPsi in file }
                            .map { clone -> manager.createProblemDescriptor(file, cloneClass, clone) }
                            .toList()
                }
                .toTypedArray()
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyArray()
        }
    }

    private fun InspectionManager.createProblemDescriptor(element: PsiElement, cloneClass: CloneClass, clone: Clone): ProblemDescriptor {
        return createProblemDescriptor(
                element,
                clone.textRange,
                PluginLabels.getMessage("inspection-problem-description",clone.firstPsi.startLine, clone.lastPsi.endLine),
                ProblemHighlightType.WEAK_WARNING,
                true,
                CloneReport(cloneClass, clone)
        )
    }

}

operator fun PsiElement.contains(element: PsiElement): Boolean =
    containingFile == element.containingFile && element.textRange in textRange

class CloneReport(val cloneClass: CloneClass, val clone: Clone) : LocalQuickFix {

    override fun getName(): String =
        PluginLabels.getMessage("inspection-fix-label", clone.firstPsi.startLine, clone.lastPsi.endLine)

    override fun getFamilyName() = PluginLabels.getMessage("inspection-fix-family-name")

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        EventQueue.invokeLater { CloneToolwindowManager.showClonesData(cloneClass) }
    }
}