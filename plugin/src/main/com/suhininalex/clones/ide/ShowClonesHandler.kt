package com.suhininalex.clones.ide

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorAction
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.suhininalex.clones.core.cloneManager
import com.suhininalex.clones.core.postprocessing.*
import com.suhininalex.clones.ide.toolwindow.CloneViewManager
import nl.komponents.kovenant.then

/**
 * Handler for action ctrl+alt+a
 */
class ShowClonesHandler : EditorAction(editorHandler)

object editorHandler : EditorActionHandler() {
    override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext) {
        val project = editor.project!!

        project.cloneManager.instance.getAllFilteredClones().then {
            CloneViewManager.showClonesData(project, it)
        }.fail {
            it.printStackTrace()
        }
    }
}

