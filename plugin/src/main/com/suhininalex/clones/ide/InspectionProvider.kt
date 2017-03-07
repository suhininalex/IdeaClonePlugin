package com.suhininalex.clones.ide

import com.intellij.codeInspection.*
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.suhininalex.clones.core.getCloneManager
import com.suhininalex.clones.core.postprocessing.*
import com.suhininalex.clones.core.structures.CloneClass
import java.awt.EventQueue
import com.suhininalex.clones.core.utils.*

class InspectionProvider : BaseJavaLocalInspectionTool() {

    override fun getGroupDisplayName() = "Analyze"

    override fun getShortName() = "CloneDetection"

    override fun getDisplayName() = "TreeClone detection"


    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor =
            CloneInspectionVisitor(holder)


    override fun isInitialized(): Boolean =
            CurrentProject?.getCloneManager()?.initialized ?: false

}

class CloneInspectionVisitor(val holder: ProblemsHolder) : JavaElementVisitor() {

    override fun visitMethod(method: PsiMethod) {
        val cloneManager = method.project.getCloneManager().instance
        val result = cloneManager.getMethodFilteredClones(method)
        result.forEach { cloneClass ->
            cloneClass.clones.filter { it.firstPsi in method  }.forEach { clone ->
                holder.registerProblem(
                        method,
                        "Method may have clones",
                        ProblemHighlightType.WEAK_WARNING,
                        clone.getTextRangeInMethod(),
                        CloneReport(cloneClass, i++)
                )
            }
        }
    }
}

var i = 1

operator fun PsiElement.contains(element: PsiElement): Boolean =
    containingFile == element.containingFile && element.textRange in textRange

class CloneReport(val cloneClass: CloneClass, val ind: Int) : LocalQuickFix {

    override fun getName() = "Show clones for this method  $ind"

    override fun getFamilyName() = "CloneReport"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        EventQueue.invokeLater { ClonesViewProvider.showClonesData(project, listOf(cloneClass)) }
    }
}