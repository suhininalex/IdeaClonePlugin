package clones;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.tree.TokenSet;
import com.suhininalex.clones.Token;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

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
