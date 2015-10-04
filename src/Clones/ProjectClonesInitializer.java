package Clones;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.ElementType;
import com.intellij.psi.tree.TokenSet;
import com.suhininalex.clones.CloneManager;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class ProjectClonesInitializer {

    private static final Map<Project, CloneManager> map = new HashMap<>();

    //TODO check this!
    public synchronized static CloneManager getInstance(Project project){
        CloneManager cloneManager = map.get(project);
        if (cloneManager!=null) return cloneManager;
        cloneManager = initializeCloneManager(project);
        map.put(project, cloneManager);
        return cloneManager;
    }

    public static CloneManager initializeCloneManager(Project project){
        CloneManager cloneManager = new CloneManager(70);
        Executor executor = Executors.newSingleThreadExecutor();
        Semaphore semaphore = new Semaphore(0);

        List<PsiFile> files = getAllPsiJavaFiles(project);
        ProgressView progressView = new ProgressView(project, files.size());
        Runnable task = () -> {
            try {
                processFiles(cloneManager, files, progressView);
                progressView.setAsProcessing();
                progressView.done();
            } catch (InterruptedException e) {
                /* Canceled! */
            } finally {
                semaphore.release();
            }
        };
        executor.execute(Utils.wrapAsReadTask(task));
        EventQueue.invokeLater(progressView::showAndGet);
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new IllegalStateException("Illegal state of clones due interruption.");
        }
        return cloneManager;
    }

    private static void processFiles(CloneManager cloneManager, List<PsiFile> files, ProgressView progressView) throws InterruptedException{
        for (final PsiFile file : files) {
            if (progressView.getStatus()==ProgressView.Status.Canceled) throw new InterruptedException("Task was canceled.");
            processPsiFile(cloneManager, file);
            progressView.next(file.getName());
        }
    }

    private static void processPsiFile(CloneManager cloneManager, PsiFile psiFile){
        for (PsiElement element : Utils.findTokens(psiFile, TokenSet.create(ElementType.METHOD))){
            cloneManager.addMethod((PsiMethod)element);
        }
    }

    private static  List<PsiFile> getAllPsiJavaFiles(Project project){
        List<PsiFile> files = new LinkedList<>();
        PsiDirectory psiDirectory = PsiManager.getInstance(project).findDirectory(project.getBaseDir());
        getPsiJavaFiles(psiDirectory, files);
        return files;
    }

    /* Auxiliary for getAllJavaFiles */
    private static void getPsiJavaFiles(PsiDirectory psiDirectory, List<PsiFile> accumulator){
        for (PsiFile file : psiDirectory.getFiles()){
            if (file instanceof PsiJavaFile)
                accumulator.add(file);
        }
        for (PsiDirectory dir : psiDirectory.getSubdirectories()){
            getPsiJavaFiles(dir, accumulator);
        }
    }

}
