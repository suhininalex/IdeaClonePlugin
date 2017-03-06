package com.suhininalex.clones.core.utils

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.suhininalex.clones.core.structures.Token
import com.suhininalex.clones.core.structures.TreeCloneClass
import com.suhininalex.clones.core.structures.Clone
import com.suhininalex.clones.ide.document
import com.suhininalex.suffixtree.Edge
import java.lang.IllegalArgumentException

fun TreeCloneClass.tokenSequence(): Sequence<Token> =
        treeNode.descTraverser().asSequence().map { it.parentEdge }.filter { it != null }.flatMap(Edge::asSequence)

@Suppress("UNCHECKED_CAST")
fun Edge.asSequence(): Sequence<Token> {
    if (isTerminal) {
        throw IllegalArgumentException("You should never invoke this method for terminating edge.")
    } else {
        return (sequence.subList(begin, end + 1) as MutableList<Token>).asSequence()
    }
}

fun Clone.getTextRangeInMethod(): TextRange {
    val methodOffset = firstPsi.method!!.textRange.startOffset
    return TextRange(firstPsi.textRange.startOffset - methodOffset, lastPsi.textRange.endOffset-methodOffset)
}

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
