package Clones;

import com.intellij.ide.SelectInEditorManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.maxgarfinkel.suffixTree.TokenRange;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by llama on 22.04.15.
 */
public class JTreeCloneNode extends DefaultMutableTreeNode {
    private final TokenRange tokenRange;

    public JTreeCloneNode(TokenRange tokenRange){
        super(getDescription(tokenRange));
        this.tokenRange = tokenRange;
    }

    @Deprecated
    public void navigate(){
        ((Navigatable)tokenRange.begin.source).navigate(true);
    }

    public void selectInEditor(){
        SelectInEditorManager.getInstance(tokenRange.begin.source.getProject())
                .selectInEditor(
                        tokenRange.begin.source.getContainingFile().getVirtualFile(),
                        tokenRange.begin.source.getTextOffset(),
                        tokenRange.end.source.getTextOffset()+tokenRange.end.source.getTextLength(),
                        false,
                        false
                );
    }

    private static String getDescription(TokenRange tokenRange){
        Document doc = tokenRange.begin.source.getContainingFile().getViewProvider().getDocument();
        int from = doc.getLineNumber(tokenRange.begin.source.getTextOffset())   +1;
        int to = doc.getLineNumber(tokenRange.end.source.getTextOffset())       +1;;
        String filename = tokenRange.begin.source.getContainingFile().getVirtualFile().getPresentableName();
        return "Lines " + from + " to " + to + " from " + filename;
    }
}
