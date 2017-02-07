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

    var lastPsi: PsiElement? = clone.firstPsi
    val all = generateSequence (clone.firstPsi) { it.findNextSibling(end) }
            .forEach {
                if ( ! it.haveSibling(end)) {
                    println("FROM: ${lastPsi!!.str} to ${it.str} LENGTH(${getLength(lastPsi!!, it)})")
                    printRange(lastPsi!!, it)
                    lastPsi = it.findNextSibling(end) //or it.parent.firstSibling
                }
            }

}

fun PsiElement.findNextSibling(maxEndOffset: Int): PsiElement? {
    return findParentWithSibling().nextSibling.findChildBeforeOffset(maxEndOffset)
}

fun PsiElement.findChildBeforeOffset(offset: Int): PsiElement? {
    var current = this
    while (current.textRange.endOffset > offset) {
//        println("FIND CHILD FOR: $str")
        current = current.firstChild ?: return null
    }
    return current
}

fun PsiElement.findParentWithSibling(): PsiElement {
    var current = this
    while (current.nextSibling == null) {
//        println("FIND PARENT FOR: ${current.str}")
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