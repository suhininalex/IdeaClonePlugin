package clones

import com.intellij.codeInspection.*
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.suhininalex.clones.*
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

        //TODO method removing!
        override fun visitMethod(method: PsiMethod) {
            val cloneManager = method.project.getCloneManager()
            cloneManager.updateMethod(method)

            cloneManager.getMethodFilteredClasses(method).forEach {
                it.clones.forEach {
                    if (it.firstElement.method.getStringId()==method.getStringId())
                        problemHolder!!.registerProblem(method, "Method may have clones", ProblemHighlightType.WEAK_WARNING, it.getTextRangeInMethod(), CloneReport.instance)
                }
            }
        }
    }

    private class CloneReport : LocalQuickFix {

        override fun getName() = "Show clones for this method"

        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            val method = descriptor.psiElement as PsiMethod
            val clones = ProjectClonesInitializer.getInstance(project).getMethodFilteredClasses(method)
            EventQueue.invokeLater { ClonesView.showClonesData(project, clones) }
        }

        override fun getFamilyName() = "CloneReport"

        companion object {
            val instance = CloneReport()
        }
    }
}