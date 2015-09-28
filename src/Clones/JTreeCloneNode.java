package Clones;

import com.intellij.ide.SelectInEditorManager;
import com.intellij.openapi.editor.Document;
import com.intellij.pom.Navigatable;
import com.suhininalex.clones.Clone;

import javax.swing.tree.DefaultMutableTreeNode;

public class JTreeCloneNode extends DefaultMutableTreeNode {
    private final Clone clone;

    public JTreeCloneNode(Clone clone){
        super(getDescription(clone));
        this.clone = clone;
    }

    @Deprecated
    public void navigate(){
        ((Navigatable) clone.firstElement.source).navigate(true);
    }

    public void selectInEditor(){
        SelectInEditorManager.getInstance(clone.firstElement.source.getProject())
                .selectInEditor(
                        clone.firstElement.source.getContainingFile().getVirtualFile(),
                        clone.firstElement.source.getTextOffset(),
                        clone.lastElement.source.getTextOffset()+ clone.lastElement.source.getTextLength(),
                        false,
                        false
                );
    }

    private static String getDescription(Clone clone){
        Document doc = clone.firstElement.source.getContainingFile().getViewProvider().getDocument();
        int from = doc.getLineNumber(clone.firstElement.source.getTextOffset())   +1;
        int to = doc.getLineNumber(clone.lastElement.source.getTextOffset())       +1;
        String filename = clone.firstElement.source.getContainingFile().getVirtualFile().getPresentableName();
        return "Lines " + from + " to " + to + " from " + filename;
    }
}
