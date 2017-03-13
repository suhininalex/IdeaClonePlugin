package com.suhininalex.clones.ide

import com.intellij.codeInspection.*
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.suhininalex.clones.core.cloneManager
import com.suhininalex.clones.core.postprocessing.*
import com.suhininalex.clones.core.structures.Clone
import com.suhininalex.clones.core.structures.CloneClass
import java.awt.EventQueue
import com.suhininalex.clones.core.utils.*
import com.suhininalex.clones.ide.toolwindow.CloneViewManager

class InspectionProvider : BaseJavaLocalInspectionTool() {

    override fun getGroupDisplayName() = "Analyze"

    override fun getShortName() = "CloneDetection"

    override fun getDisplayName() = "TreeClone detection"


    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor =
            CloneInspectionVisitor(holder)

    override fun isInitialized(): Boolean =
            CurrentProject?.cloneManager?.initialized ?: false

}

class CloneInspectionVisitor(val holder: ProblemsHolder) : JavaElementVisitor() {

    override fun visitMethod(method: PsiMethod) {
        val cloneManager = method.project.cloneManager.instance
        val result = cloneManager.getMethodFilteredClones(method)
        result.forEach { cloneClass ->
            cloneClass.clones.filter { it.firstPsi in method  }.forEach { clone ->
                holder.registerProblem(
                        method,
                        "Method may have cloneClasses",
                        ProblemHighlightType.WEAK_WARNING,
                        clone.getTextRangeInMethod(),
                        CloneReport(cloneClass, clone)
                )
            }
        }
    }
}

operator fun PsiElement.contains(element: PsiElement): Boolean =
    containingFile == element.containingFile && element.textRange in textRange

class CloneReport(val cloneClass: CloneClass, val clone: Clone) : LocalQuickFix {

    override fun getName() = "Show cloneClasses for this method ${clone.firstPsi.startLine}:${clone.lastPsi.endLine}"

    override fun getFamilyName() = "CloneReport"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        EventQueue.invokeLater { CloneViewManager.showClonesData(project, cloneClass) }
    }
}