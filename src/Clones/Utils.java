package Clones;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.java.stubs.JavaStubElementTypes;
import com.intellij.psi.impl.source.tree.ElementType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.maxgarfinkel.suffixTree.SuffixTree;
import com.maxgarfinkel.suffixTree.Method;
import com.maxgarfinkel.suffixTree.Token;
import groovy.io.FileType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by llama on 25.02.15.
 */
public class Utils {

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
        List<Token> tokens = new LinkedList<Token>();
        Method newMethod = new Method(tokens);
        makeTokenSequence(newMethod, tokens, root, filter);
        return tokens;
    }

    private static void makeTokenSequence(@NotNull Method method, @NotNull List<Token> accumulator, @NotNull PsiElement node, @NotNull TokenSet filter){
        if (filter.contains(node.getNode().getElementType())) return;
        accumulator.add(new Token(node, method, accumulator.size()));
        for (PsiElement child : node.getChildren()){
            makeTokenSequence(method, accumulator, child, filter);
        }
    }

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

    public static void printTreeToFile(SuffixTree<Token, Iterable<Token>> trie, String filename){
        File file = new File("/home/llama/"+filename);
        try {
            PrintWriter pw = new PrintWriter(file);
            pw.println(trie);
            pw.close();
        } catch (IOException e){
            throw new RuntimeException("Cannot create file: "+filename);
        }
    }

    @Deprecated
    public static List<Token> makeTreeSequence(PsiFile psiFile){
        LinkedList<Token> sequence = new LinkedList<Token>();
        for (PsiElement element : Utils.findTokens(psiFile, TokenSet.create(JavaStubElementTypes.METHOD))){
            sequence.addAll(Utils.makeTokenSequence(element,TokenSet.EMPTY));
        }
        return sequence;
    }
}
