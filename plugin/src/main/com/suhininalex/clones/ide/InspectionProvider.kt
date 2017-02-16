package com.suhininalex.clones.ide

import com.intellij.codeInspection.*
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.suhininalex.clones.core.*
import com.suhininalex.clones.core.clonefilter.filterClones
import java.awt.EventQueue

class InspectionProvider : BaseJavaLocalInspectionTool() {

    override fun getGroupDisplayName() = "Analyze"

    override fun getShortName() = "CloneDetection"

    override fun getDisplayName() = "TreeClone detection"

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor =
            CloneInspectionVisitor(holder)
}

class CloneInspectionVisitor(val holder: ProblemsHolder) : JavaElementVisitor() {

    val cloneReport = CloneReport()

    override fun visitMethod(method: PsiMethod) {
        val cloneManager = method.project.getCloneManager()
        val result = cloneManager.getAllMethodClasses(method)
                .filterClones().extractSiblingClones()

        filterSameCloneRangeClasses(result)
                .forEach {
                    it.cloneRanges.forEach {
                        if (it.firstPsi in method)
                            holder.registerProblem(method, "Method may have clones", ProblemHighlightType.WEAK_WARNING, it.getTextRangeInMethod(method.textRange.startOffset), cloneReport)
                    }
                }
    }
}

operator fun PsiElement.contains(element: PsiElement): Boolean =
    if (containingFile == element.containingFile)
        element.textRange.startOffset in textRange
    else false

class CloneReport : LocalQuickFix {

    override fun getName() = "Show clones for this method"

    override fun getFamilyName() = "CloneReport"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val method = descriptor.psiElement as PsiMethod
        val clones = project.getCloneManager().getAllMethodClasses(method).filterClones().extractSiblingClones()
        val c2 = filterSameCloneRangeClasses(clones)
        EventQueue.invokeLater { ClonesViewProvider.showClonesData(project, c2) }
    }
}