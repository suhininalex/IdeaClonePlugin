package com.suhininalex.clones.ide

import com.intellij.codeInspection.*
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.suhininalex.clones.core.clonefilter.filterClones
import com.suhininalex.clones.core.getCloneManager
import com.suhininalex.clones.core.getTextRangeInMethod
import com.suhininalex.clones.core.stringId
import java.awt.EventQueue

class InspectionProvider : BaseJavaLocalInspectionTool() {

    override fun getGroupDisplayName() = "Analyze"

    override fun getShortName() = "CloneDetection"

    override fun getDisplayName() = "Clone detection"

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor =
            CloneInspectionVisitor(holder)
}

class CloneInspectionVisitor(val holder: ProblemsHolder) : JavaElementVisitor() {

    val cloneReport = CloneReport()

    override fun visitMethod(method: PsiMethod) {
        val cloneManager = method.project.getCloneManager()
        cloneManager.getAllMethodClasses(method)
                .filterClones()
                .forEach {
                    it.clones.forEach {
                        if (it.firstElement.method.stringId == method.stringId)
                            holder.registerProblem(method, "Method may have clones", ProblemHighlightType.WEAK_WARNING, it.getTextRangeInMethod(method.textRange.startOffset), cloneReport)
                    }
                }
    }
}

class CloneReport : LocalQuickFix {

    override fun getName() = "Show clones for this method"

    override fun getFamilyName() = "CloneReport"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val method = descriptor.psiElement as PsiMethod
        val clones = project.getCloneManager().getAllMethodClasses(method).filterClones()
        EventQueue.invokeLater { ClonesViewProvider.showClonesData(project, clones.toList()) }
    }
}