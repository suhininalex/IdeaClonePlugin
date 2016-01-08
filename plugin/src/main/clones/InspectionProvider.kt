package clones

import com.intellij.codeInspection.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import org.jetbrains.annotations.Nls

import java.awt.*


class InspectionProvider : BaseJavaLocalInspectionTool() {

    companion object {
        internal var problemHolder: ProblemsHolder? = null
    }

    @Nls
    override fun getGroupDisplayName() = "Analyze"

    override fun getShortName() = "CloneDetection"

    @Nls
    override fun getDisplayName() = "Clone detection"

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        problemHolder = holder
        return CloneInspectionVisitor()
    }

    private class CloneInspectionVisitor : JavaElementVisitor() {

        //TODO clones highlight & method removing!
        override fun visitMethod(method: PsiMethod) {
            val cloneManager = ProjectClonesInitializer.getInstance(method.project)
            cloneManager.updateMethod(method)
            val clones = cloneManager.getMethodFilteredClones(method)
            if (!clones.isEmpty()) {
                val textRange = TextRange(method.modifierList.textRange.startOffset - method.textRange.startOffset, method.parameterList.textRange.endOffset - method.textRange.startOffset)
                problemHolder!!.registerProblem(method, textRange, "Method may have clones", CloneReport.instance)
            }
        }
    }

    private class CloneReport : LocalQuickFix {

        override fun getName() = "Show clones for this method"

        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            val method = descriptor.psiElement as PsiMethod
            val clones = ProjectClonesInitializer.getInstance(project).getMethodFilteredClones(method)
            EventQueue.invokeLater { ClonesView.showClonesData(project, clones) }
        }

        override fun getFamilyName() = "CloneReport"

        companion object {
            val instance = CloneReport()
        }
    }
}