import clones.ClonesView;
import clones.ClonesViewProvider;
import clones.ProjectClonesInitializer;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.project.Project;


/**
 * Handler for action ctrl+alt+a
 */
public class CopyLineUpAction extends EditorAction {

    public CopyLineUpAction() {
        super(new EditorHandler());
    }

    private static class EditorHandler extends EditorActionHandler {
        @Override
        protected void doExecute(Editor editor, Caret caret, DataContext dataContext) {
            Project project = editor.getProject();
            ClonesViewProvider.INSTANCE.showClonesData(project, ProjectClonesInitializer.INSTANCE.getInstance(project).getAllFilteredClones());
        }
    }


}
