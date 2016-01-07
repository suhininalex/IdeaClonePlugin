package clones;

import com.intellij.codeInspection.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.suhininalex.clones.CloneClass;
import com.suhininalex.clones.CloneManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;


public class InspectionProvider extends BaseJavaLocalInspectionTool {

    static ProblemsHolder holder;

    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return "Analyze";
    }

    @NotNull
    public String getShortName() {
        return "CloneDetection";
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Clone detection";
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        this.holder = holder;
        return new CloneInspectionVisitor();
    }

    private static class CloneInspectionVisitor extends JavaElementVisitor {

        //TODO clones highlight & method removing!
        @Override
        public void visitMethod(PsiMethod method) {
            CloneManager cloneManager = ProjectClonesInitializer.getInstance(method.getProject());
            cloneManager.updateMethod(method);
            List<CloneClass> clones = cloneManager.getMethodFilteredClones(method);
            if (!clones.isEmpty()) {
                TextRange textRange =  new TextRange(method.getModifierList().getTextRange().getStartOffset() - method.getTextRange().getStartOffset(), method.getParameterList().getTextRange().getEndOffset() -  method.getTextRange().getStartOffset());
                holder.registerProblem(method, textRange, "Method may have clones", CloneReport.getInstance());
            }
        }
    }


    private static class CloneReport implements LocalQuickFix {

        static final CloneReport instance = new CloneReport();

        public static CloneReport getInstance() {
            return instance;
        }

        @NotNull
        public String getName() {
            return "Show clones for this method";
        }

        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiMethod method = (PsiMethod)descriptor.getPsiElement();
            List<CloneClass> clones =  ProjectClonesInitializer.getInstance(project).getMethodFilteredClones(method);
            EventQueue.invokeLater(() -> ClonesView.showClonesData(project, clones));
        }

        @NotNull
        public String getFamilyName() {
            return "CloneReport";
        }
    }
}

