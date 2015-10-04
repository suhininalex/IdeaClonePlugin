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

    static AllManager allManager;
    static ProblemsHolder holder;



    synchronized  static public AllManager getViewManager(Project project){
        if (allManager ==null) allManager = new AllManager(project);
        return allManager;
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
            allManager.cloneManager.updateMethod(method);
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

