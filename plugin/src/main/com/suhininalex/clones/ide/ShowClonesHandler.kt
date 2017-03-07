package com.suhininalex.clones.ide

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorAction
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.suhininalex.clones.core.getCloneManager
import com.suhininalex.clones.core.postprocessing.*
import nl.komponents.kovenant.then

/**
 * Handler for action ctrl+alt+a
 */
class ShowClonesHandler : EditorAction(editorHandler)

object editorHandler : EditorActionHandler() {
    override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext) {
        val project = editor.project!!

        project.getCloneManager().instance.getAllFilteredClones().then {
            ClonesViewProvider.showClonesData(project, it)
        }
    }
}

