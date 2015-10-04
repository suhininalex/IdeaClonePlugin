import Clones.AllManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;


public class CopyLineUpAction extends EditorAction {

    public CopyLineUpAction() {
        super(new EditorHandler());
    }



    private static class EditorHandler extends EditorActionHandler {
        @Override
        protected void doExecute(Editor editor, Caret caret, DataContext dataContext) {
            Project project = editor.getProject();

            AllManager allManager = new AllManager(project);
            allManager.showProjectClones();
        }

    }


}
