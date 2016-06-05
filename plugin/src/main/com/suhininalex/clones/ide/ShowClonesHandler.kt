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
    }
}


//project.getCloneManager()

//println("Update testing:")
//val times = project.getAllPsiJavaFiles().flatMap { it.findTokens(TokenSet.create(ElementType.METHOD)) }.map {
//    val t = elapsedTime {
//        project.getCloneManager().getMethodFilteredClasses(it as PsiMethod)
//    }
//    println("Done: $t")
//    t
//}
//        .skip(1).limit(5000).toList()
//
//println("MAX: ${times.max()}")
//println("Average: ${times.average()}")
//println("Med: ${times.sorted()[times.size/2]}")