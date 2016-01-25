package clones

import com.intellij.ide.SelectInEditorManager
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.suhininalex.clones.Clone
import javax.swing.tree.DefaultMutableTreeNode

class ViewTreeNode(val clone: Clone) : DefaultMutableTreeNode(clone.getDescription()) {

    fun selectInEditor() = with (clone) {
        SelectInEditorManager.getInstance(project).selectInEditor(
            file,
            firstPsi.textOffset,
            lastPsi.textRange.endOffset,
            false,
            false
        )
    }
}

fun Clone.getDescription() = "Lines ${firstPsi.lineNumber} to ${lastPsi.lineNumber} from ${file.presentableName}"

val Clone.firstPsi: PsiElement get() = firstElement.source

val Clone.lastPsi: PsiElement get() = lastElement.source

val Clone.file: VirtualFile get() = firstPsi.containingFile.virtualFile

val Clone.project: Project get() = firstPsi.project

val PsiElement.document: Document get() = containingFile.viewProvider.document!!

val PsiElement.lineNumber: Int get() = document.getLineNumber(textOffset) + 1
