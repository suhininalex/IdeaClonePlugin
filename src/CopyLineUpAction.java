import Clones.CloneManager;
import Clones.ClonesView;
import Clones.ProgressView;
import com.intellij.execution.RunManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.components.JBScrollPane;
import com.maxgarfinkel.suffixTree.*;
import Clones.Utils;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;

import com.suhininalex.suffixtree.*;
import com.suhininalex.suffixtree.*;
import com.suhininalex.suffixtree.SuffixTree;
import org.jetbrains.annotations.NotNull;
import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by llama on 09.02.15.
 */
public class CopyLineUpAction extends EditorAction {

    public CopyLineUpAction() {
        super(new EditorHandler());
    }



    private static class EditorHandler extends EditorActionHandler {
        @Override
        protected void doExecute(Editor editor, Caret caret, DataContext dataContext) {
            Project project = editor.getProject();

            CloneManager cloneManager = new CloneManager(project);
            cloneManager.showProjectClones();


//            StringBuilder string = new StringBuilder();
//            for (CloneClass clones : clonesList){
//                string.append(clones);
//            }
//            Utils.printToFile(sequence, "problemsequence.txt");
//            Utils.printTreeToFile(tree, "trie.txt");
//            TokenSet filter = TokenSet.create(ElementType.WHITE_SPACE,ElementType.SEMICOLON);
//            Utils.printToFile(Utils.makeTokenSequence(psiFile,filter),"tokens.txt");
//            Utils.printStringToFile(string.toString(),"clones.txt");
//            Messages.showInfoMessage("Breaker", "I'm title!");
        }

    }


}
