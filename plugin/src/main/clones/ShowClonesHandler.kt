import clones.ClonesViewProvider
import clones.ProjectClonesInitializer
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorAction
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.suhininalex.clones.getCloneManager
import com.suhininalex.clones.toList

/**
 * Handler for action ctrl+alt+a
 */
class ShowClonesHandler : EditorAction(ShowClonesHandler.EditorHandler()) {

    private class EditorHandler : EditorActionHandler() {
        override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext) {
            val project = editor.project!!
            project.getCloneManager().filteredClonesApply { ClonesViewProvider.showClonesData(project, it) }
        }
    }
}