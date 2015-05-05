package Clones;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.AsyncResult;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.ElementType;
import com.intellij.psi.tree.TokenSet;
import com.maxgarfinkel.suffixTree.CloneClass;
import com.maxgarfinkel.suffixTree.SuffixTree;
import com.maxgarfinkel.suffixTree.Token;
import com.maxgarfinkel.suffixTree.TrieManager;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CloneManager {
    private final Project project;
    private Executor executor = Executors.newSingleThreadExecutor();
    private final SuffixTree<Token,Iterable<Token>>  suffixTree= new SuffixTree<Token,Iterable<Token>>();

    public CloneManager(Project project) {
        this.project = project;
    }

    public List<CloneClass> getClones(){
        List<CloneClass> clonesList = TrieManager.getClones(suffixTree);
        TrieManager.filterSomeClones(clonesList);
        return clonesList;
    }

    static int i=0;
    public void processAllProject(){
        final List<PsiFile> files = getAllPsiJavaFiles(project);
        final ProgressView progressView = new ProgressView(project, files.size());
        Runnable task = new Runnable() {
            @Override
            public void run(){
                for (final PsiFile file : files) {
                    processPsiFile(file);
                    progressView.next(file.getName());
                    System.out.println(i++);
                }
                progressView.clickDefaultButton();
            }
        };
        executor.execute(new ReadTaskWrapper(task));
        progressView.show();
    }

    private void processPsiFile(@NotNull final PsiFile psiFile){
        TokenSet filter = TokenSet.create(ElementType.WHITE_SPACE,ElementType.SEMICOLON);
        for (PsiElement element : Utils.findTokens(psiFile, TokenSet.create(ElementType.METHOD))){
            suffixTree.add(Utils.makeTokenSequence(element, filter));
        }
    }

    private  List<PsiFile> getAllPsiJavaFiles(Project project){
        List<PsiFile> files = new LinkedList<PsiFile>();
        PsiDirectory psiDirectory = PsiManager.getInstance(project).findDirectory(project.getBaseDir());
        getPsiJavaFiles(psiDirectory,files);
        return files;
    }

    /* Auxiliary for getAllJavaFiles */
    private void getPsiJavaFiles(@NotNull PsiDirectory psiDirectory, List<PsiFile> accumulator){
        for (PsiFile file : psiDirectory.getFiles()){
            if (file instanceof PsiJavaFile)
                accumulator.add(file);
        }
        for (PsiDirectory dir : psiDirectory.getSubdirectories()){
            getPsiJavaFiles(dir, accumulator);
        }
    }

    private static class ReadTaskWrapper implements Runnable{
        private final Runnable runnable;

        @Override
        public void run() {
            ApplicationManager.getApplication().runReadAction(runnable);
        }

        public ReadTaskWrapper(Runnable runnable) {
            this.runnable = runnable;


        }
    }


}
