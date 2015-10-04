package Clones;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.ElementType;
import com.intellij.psi.tree.TokenSet;
import com.suhininalex.clones.CloneManager;
import sun.awt.Mutex;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AllManager {

    private final Project project;
    private final Executor executor = Executors.newSingleThreadExecutor();
    public  final CloneManager cloneManager = new CloneManager(70);

    public AllManager(final Project project) {
        this.project = project;
    }

    public void showProjectClones(){
        final List<PsiFile> files = getAllPsiJavaFiles(project);
        final Semaphore semaphore = new Semaphore(0);
        final ProgressView progressView = new ProgressView(project, files.size());
        Runnable task = () -> {
            try {
                processFiles(files, progressView);
                progressView.setAsProcessing();
                ClonesView.showClonesData(project, cloneManager.getAllFilteredClones());
                progressView.done();
            } catch (InterruptedException e) {
                /* Canceled! */
            } finally {
                semaphore.release();
            }
        };
        executor.execute(Utils.wrapAsReadTask(task));
        progressView.showAndGet();
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {

        }
    }

    public void initialize(){

    }


    private void processFiles(List<PsiFile> files, ProgressView progressView) throws InterruptedException{
        for (final PsiFile file : files) {
            if (progressView.getStatus()==ProgressView.Status.Canceled) throw new InterruptedException("Task was canceled.");
            processPsiFile(file);
            progressView.next(file.getName());
        }
    }

    private void processPsiFile(PsiFile psiFile){

        for (PsiElement element : Utils.findTokens(psiFile, TokenSet.create(ElementType.METHOD))){
            cloneManager.addMethod((PsiMethod)element);
        }
    }

    private  List<PsiFile> getAllPsiJavaFiles(Project project){
        List<PsiFile> files = new LinkedList<>();
        PsiDirectory psiDirectory = PsiManager.getInstance(project).findDirectory(project.getBaseDir());
        getPsiJavaFiles(psiDirectory, files);
        return files;
    }

    /* Auxiliary for getAllJavaFiles */
    private void getPsiJavaFiles(PsiDirectory psiDirectory, List<PsiFile> accumulator){
        for (PsiFile file : psiDirectory.getFiles()){
            if (file instanceof PsiJavaFile)
                accumulator.add(file);
        }
        for (PsiDirectory dir : psiDirectory.getSubdirectories()){
            getPsiJavaFiles(dir, accumulator);
        }
    }

}
