package com.suhininalex.clones.ide

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorAction
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.suhininalex.clones.core.*
import com.suhininalex.clones.core.clonefilter.filterWithProgressbar
import nl.komponents.kovenant.task
import nl.komponents.kovenant.then
import java.awt.EventQueue

/**
 * Handler for action ctrl+alt+a
 */
class ShowClonesHandler : EditorAction(editorHandler)

object editorHandler : EditorActionHandler() {
    override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext) {
        val project = editor.project!!

        task {
            project.getCloneManager().getAllCloneClasses().toList()
                .filterWithProgressbar()
                .then {
                    Application.runReadAction {
                        val result = it.extractSiblingClones().filter {
                            val score = it.getScore()
                            if (score.selfCoverage > 0.85) false
                            else if (score.selfCoverage > 0.7 && score.sameMethodCount > 0.7) false
                            else true
                        }
                        ClonesViewProvider.showClonesData(project, filterSameCloneRangeClasses(result))
                    }
                }
        }
    }
}