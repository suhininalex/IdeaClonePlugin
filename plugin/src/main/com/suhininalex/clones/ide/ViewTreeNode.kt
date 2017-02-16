package com.suhininalex.clones.ide

import com.intellij.ide.SelectInEditorManager
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.suhininalex.clones.core.RangeClone
import com.suhininalex.clones.core.interfaces.Clone
import javax.swing.tree.DefaultMutableTreeNode

class ViewTreeNode(val clone: Clone) : DefaultMutableTreeNode(clone.getDescription())

fun Clone.selectInEditor(){
    SelectInEditorManager.getInstance(project).selectInEditor(
            file,
            firstPsi.textRange.startOffset,
            lastPsi.textRange.endOffset,
            false,
            false
    )
}

fun Clone.getDescription() = "Lines ${firstPsi.startLine} to ${lastPsi.endLine} from ${file.presentableName} (${file.hashCode()})"

val Clone.file: VirtualFile
    get() = firstPsi.containingFile.virtualFile

val Clone.project: Project
    get() = firstPsi.project

val PsiElement.document: Document
    get() = containingFile.viewProvider.document!!

val PsiElement.startLine: Int
    get() = document.getLineNumber(textRange.startOffset) + 1

val PsiElement.endLine: Int
    get() = document.getLineNumber(textRange.endOffset) + 1