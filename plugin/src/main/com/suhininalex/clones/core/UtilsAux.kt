package com.suhininalex.clones.core

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.suhininalex.clones.ide.document
import com.suhininalex.clones.ide.firstPsi

fun splitToSiblings(clones: List<CloneClass>){
    val cloneClass = clones[0]
    val clone = cloneClass.clones.first()
    val tokens = cloneClass.tokenSequence().map { it.source.node.elementType }.toList()
    println(tokens)

    val end = clone.lastElement.source.textRange.endOffset
    val start = clone.firstElement.source.textRange.startOffset

    val all = generateSequence (clone.firstPsi) { it.findNextSibling(end) }
            .filter { ! it.haveSibling(end) }
            .map {
                    val firstSibling = it.parent.firstChild
                    val leftPsi = if (firstSibling.textRange.startOffset < start) clone.firstPsi else firstSibling
                    leftPsi to it
            }.forEach { (leftPsi, rightPsi) ->
                println("FROM: ${leftPsi.str} to ${rightPsi.str} LENGTH(${getLength(leftPsi, rightPsi)})")
                printRange(leftPsi, rightPsi)
            }
//    TODO("last string problem")

}

fun PsiElement.findNextSibling(maxEndOffset: Int): PsiElement? {
    return findParentWithSibling().nextSibling.findChildBeforeOffset(maxEndOffset)
}

fun PsiElement.findChildBeforeOffset(offset: Int): PsiElement? {
    var current = this
    while (current.textRange.endOffset > offset) {
        current = current.firstChild ?: return null
    }
    return current
}

fun PsiElement.findParentWithSibling(): PsiElement {
    var current = this
    while (current.nextSibling == null) {
        current = current.parent
    }
    return current
}

val PsiElement.str: String
    get() = "${node.elementType}"

fun getLength(firstPsi: PsiElement, secondPsi: PsiElement): Int =
        secondPsi.textRange.endOffset - firstPsi.textRange.startOffset

fun printRange(firstPsi: PsiElement, secondPsi: PsiElement){
    val range = TextRange(firstPsi.textRange.startOffset, secondPsi.textRange.endOffset)
    println(firstPsi.document.getText(range))
}

fun PsiElement.haveSibling(maxEndOffset: Int): Boolean {
    return nextSibling != null && nextSibling.textRange.endOffset < maxEndOffset
}