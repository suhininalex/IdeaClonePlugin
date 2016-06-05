package com.suhininalex.clones.ide

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorAction
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.suhininalex.clones.core.getCloneManager

/**
 * Handler for action ctrl+alt+a
 */
class ShowClonesHandler : EditorAction(editorHandler)

object editorHandler : EditorActionHandler() {
    override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext) {
        val project = editor.project!!
        project.getCloneManager().filteredClonesApply { ClonesViewProvider.showClonesData(project, it) }

//        println("Update testing:")
//        val times = project.getAllPsiJavaFiles().flatMap { it.findTokens(TokenSet.create(ElementType.METHOD)) }.map {
//            elapsedTime {
//                project.getCloneManager().updateMethod(it as PsiMethod)
//                project.getCloneManager().getMethodFilteredClasses(it)
//            }
//        }
//        .limit(5000).toList()
//
//        println("MAX: ${times.max()}")
//        println("Average: ${times.average()}")
//        println("Med: ${times.sorted()[times.size/2]}")
    }
}