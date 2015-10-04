package Clones;

import com.intellij.codeInspection.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.suhininalex.clones.CloneClass;
import com.suhininalex.clones.CloneManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;


public class InspectionProvider extends BaseJavaLocalInspectionTool {

    static volatile AllManager allManager;
    static ProblemsHolder holder;



    synchronized static public AllManager getViewManager(Project project){
        if (allManager==null) {
            allManager = new AllManager(project);
            allManager.showProjectClones();
        }
        return allManager;
    }

    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return "Analyze";
    }

    @NotNull
    public String getShortName() {
        return "Clone detection";
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
            synchronized (InspectionProvider.class) {
//                    EventQueue.invokeLater(() -> getViewManager(holder.getProject()).showProjectClones());
            }
        getViewManager(holder.getProject());
        this.holder = holder;
        return new MyVisitor();
    }

    private static class MyVisitor extends JavaElementVisitor {

        @Override
        public void visitMethod(PsiMethod method) {

//            System.out.println("In method: " + Utils.getMethodId(method));
            allManager.cloneManager.updateMethod(method);
            List<CloneClass> clones = getViewManager(method.getProject()).cloneManager.getMethodFilteredClones(method);
            if (!clones.isEmpty())
                holder.registerProblem(method, "Atatata problem!", new MyQuickFix());
        }

    }


    private static class MyQuickFix implements LocalQuickFix {

        @NotNull
        public String getName() {
            return "Show all clone classes";
        }

        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiMethod method = (PsiMethod)descriptor.getPsiElement();
            CloneManager cm = getViewManager(project).cloneManager;
            List<CloneClass> clones =  cm.getMethodFilteredClones(method);
            EventQueue.invokeLater(() -> ClonesView.showClonesData(project, clones));
        }

        @NotNull
        public String getFamilyName() {
            return "myfix";
        }
    }
}

