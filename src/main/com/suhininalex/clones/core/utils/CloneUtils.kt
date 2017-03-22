package com.suhininalex.clones.core.utils

import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.suhininalex.clones.core.languagescope.LanguageIndexedPsiManager
import com.suhininalex.clones.core.structures.Token
import com.suhininalex.clones.core.structures.TreeCloneClass
import com.suhininalex.clones.core.structures.Clone
import com.suhininalex.clones.core.structures.CloneClass
import com.suhininalex.suffixtree.Edge
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
fun Edge.asSequence(): Sequence<Token> {
    if (isTerminal) {
        throw IllegalArgumentException("You should never invoke this method for terminating edge.")
    } else {
        return (sequence.subList(begin, end + 1) as MutableList<Token>).asSequence()
    }
}

fun Clone.getTextRangeInIndexedFragment(): TextRange {
    val indexedParent = LanguageIndexedPsiManager.getIndexedPsiDefiner(firstPsi)?.getIndexedParent(firstPsi)!!

    val methodOffset = indexedParent.textRange.startOffset
    return TextRange(firstPsi.textRange.startOffset - methodOffset, lastPsi.textRange.endOffset-methodOffset)
}

val Clone.textRange: TextRange
    get() = TextRange(firstPsi.textRange.startOffset, lastPsi.textRange.endOffset)

fun Clone.printText(){
    val range = TextRange(firstPsi.textRange.startOffset, lastPsi.textRange.endOffset)
    println(firstPsi.document.getText(range))
}

fun Clone.tokenSequence(): Sequence<PsiElement> =
        generateSequence (firstPsi.firstEndChild()) { it.nextLeafElement() }
                .takeWhile { it.textRange.endOffset <= lastPsi.textRange.endOffset }
                .filterNot( ::isNoiseElement)

val Clone.textLength: Int
        get() = lastPsi.textRange.endOffset - firstPsi.textRange.startOffset

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

val CloneClass.project: Project
    get() = clones.first().project