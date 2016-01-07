package clones;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.*;
import com.intellij.psi.impl.java.stubs.JavaStubElementTypes;
import com.intellij.psi.tree.TokenSet;
import com.suhininalex.clones.Token;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Utils {

    //TODO обеспечить уникальность (все еще нет!)
    public static String getMethodId(PsiMethod method){
        return method.getContainingFile().getContainingDirectory().getName() + "." +method.getContainingClass().getName() + "." + method.getName()+method.getParameterList().toString();

    }

    public static List<PsiElement> findTokens(@NotNull PsiElement root, @NotNull TokenSet tokenSet){
        List<PsiElement> psiList = new LinkedList<PsiElement>();
        findTokens(psiList,root,tokenSet);
        return psiList;
    }

    private static void findTokens(@NotNull List<PsiElement> accumulator, @NotNull PsiElement node, @NotNull TokenSet tokenSet){
        if (node.getNode()==null) return;
        if (tokenSet.contains(node.getNode().getElementType())) {
            accumulator.add(node);
            return;
        }
        for (PsiElement child : node.getChildren()){
            findTokens(accumulator, child, tokenSet);
        }
    }

    public static List<Token> makeTokenSequence(@NotNull PsiElement root, @NotNull TokenSet filter) {
        List<Token> tokens = new LinkedList<>();
        makeTokenSequence(tokens, root, filter);
        return tokens;
    }

    private static void makeTokenSequence(@NotNull List<Token> accumulator, @NotNull PsiElement node, @NotNull TokenSet filter){
        if (filter.contains(node.getNode().getElementType())) return;
        accumulator.add(new Token(node, accumulator.size()));
        for (PsiElement child : node.getChildren()){
            makeTokenSequence(accumulator, child, filter);
        }
    }

    @Deprecated
    public static void printToFile(List<Token> list, String filename){
        File file = new File("/home/llama/"+filename);

        try {
            PrintWriter pw = new PrintWriter(file);
            for (Token token : list) {
                pw.println(token.source.getNode().getElementType().getIndex() + "| "+token.source.getNode().getElementType() + " | "+token.source.getNode().getElementType().getClass());
            }
            pw.close();
        } catch (IOException e){
            throw new RuntimeException("Cannot create file: "+filename);
        }
    }

    public static void printStringToFile(String string, String filename){
        File file = new File("/home/llama/"+filename);

        try {
            PrintWriter pw = new PrintWriter(file);
            pw.println(string);
            pw.close();
        } catch (IOException e){
            throw new RuntimeException("Cannot create file: "+filename);
        }
    }

    @Deprecated
    public static List<Token> makeTreeSequence(PsiFile psiFile){
        LinkedList<Token> sequence = new LinkedList<>();
        for (PsiElement element : Utils.findTokens(psiFile, TokenSet.create(JavaStubElementTypes.METHOD))){
            sequence.addAll(Utils.makeTokenSequence(element,TokenSet.EMPTY));
        }
        return sequence;
    }

    public static Runnable wrapAsReadTask(Runnable task){
        return () -> ApplicationManager.getApplication().runReadAction(task);
    }
}
