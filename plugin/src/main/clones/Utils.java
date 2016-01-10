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
    public static List<Token> makeTokenSequence(@NotNull PsiElement root, @NotNull TokenSet filter, PsiMethod method) {
        List<Token> tokens = new LinkedList<>();
        makeTokenSequence(tokens, root, filter, method);
        return tokens;
    }

    private static void makeTokenSequence(@NotNull List<Token> accumulator, @NotNull PsiElement node, @NotNull TokenSet filter, PsiMethod method){
        if (filter.contains(node.getNode().getElementType())) return;
        accumulator.add(new Token(node, method));
        for (PsiElement child : node.getChildren()){
            makeTokenSequence(accumulator, child, filter, method);
        }
    }


}
