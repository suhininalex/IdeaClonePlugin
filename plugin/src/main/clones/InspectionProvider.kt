package clones

import com.intellij.codeInspection.*
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiMethod
import com.suhininalex.clones.getCloneManager
import com.suhininalex.clones.getStringId
import com.suhininalex.clones.getTextRangeInMethod
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

    //TODO method removing!
    override fun visitMethod(method: PsiMethod) {
        val cloneManager = method.project.getCloneManager()
        cloneManager.updateMethod(method)

        cloneManager.getMethodFilteredClasses(method).forEach {
            it.clones.forEach {
                if (it.firstElement.method.getStringId()==method.getStringId())
                    holder.registerProblem(method, "Method may have clones", ProblemHighlightType.WEAK_WARNING, it.getTextRangeInMethod(), cloneReport)
            }
        }
    }
}

class CloneReport : LocalQuickFix {

    override fun getName() = "Show clones for this method"

    override fun getFamilyName() = "CloneReport"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val method = descriptor.psiElement as PsiMethod
        val clones = ProjectClonesInitializer.getInstance(project).getMethodFilteredClasses(method)
        EventQueue.invokeLater { ClonesViewProvider.showClonesData(project, clones) }
    }
}