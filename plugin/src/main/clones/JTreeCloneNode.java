package clones;

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
        ((Navigatable) clone.getFirstElement().getSource()).navigate(true);
    }

    public void selectInEditor(){
        SelectInEditorManager.getInstance(clone.getFirstElement().getSource().getProject())
                .selectInEditor(
                        clone.getFirstElement().getSource().getContainingFile().getVirtualFile(),
                        clone.getFirstElement().getSource().getTextOffset(),
                        clone.getLastElement().getSource().getTextOffset()+ clone.getLastElement().getSource().getTextLength(),
                        false,
                        false
                );
    }

    private static String getDescription(Clone clone){
        Document doc = clone.getFirstElement().getSource().getContainingFile().getViewProvider().getDocument();
        int from = doc.getLineNumber(clone.getFirstElement().getSource().getTextOffset())   +1;
        int to = doc.getLineNumber(clone.getLastElement().getSource().getTextOffset())       +1;
        String filename = clone.getFirstElement().getSource().getContainingFile().getVirtualFile().getPresentableName();
        return "Lines " + from + " to " + to + " from " + filename;
    }
}
