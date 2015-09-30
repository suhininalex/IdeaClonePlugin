package Clones;

import com.intellij.codeInspection.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.suhininalex.clones.CloneClass;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;


public class InspectionProvider extends BaseJavaLocalInspectionTool {

    static ViewManager viewManager;
    static ProblemsHolder holder;



    synchronized  static public ViewManager getViewManager(Project project){
        if (viewManager==null) viewManager = new ViewManager(project);
        return viewManager;
    }

    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return "Clones!";
    }

    @NotNull
    public String getShortName() {
        return "CloneDetection";
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Suhinin clone detection tool!";
    }


    static volatile boolean visited = false;

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        if (!visited) {
            synchronized (InspectionProvider.class) {
                if (!visited)
                    EventQueue.invokeLater(() -> getViewManager(holder.getProject()).showProjectClones());
            }
        }
        while (!visited);
        this.holder = holder;
        return new MyVisitor();
    }

    private static class MyVisitor extends JavaElementVisitor {

        @Override
        public void visitMethod(PsiMethod method) {

//            System.out.println("In method: " + Utils.getMethodId(method));
            List<CloneClass> clones = getViewManager(method.getProject()).cloneManager.getMethodFilteredClones(method);
            if (!clones.isEmpty())
                holder.registerProblem(method, "Atatata problem!", new MyQuickFix());
        }

    }


    private static class MyQuickFix implements LocalQuickFix {

        @NotNull
        public String getName() {
            return "Clone detected!";
        }

        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiMethod method = (PsiMethod)descriptor.getPsiElement();
            List<CloneClass> clones =  getViewManager(project).cloneManager.getMethodFilteredClones(method);
            EventQueue.invokeLater(() -> ClonesView.showClonesData(project, clones));
        }

        @NotNull
        public String getFamilyName() {
            return "myfix";
        }
    }
}

