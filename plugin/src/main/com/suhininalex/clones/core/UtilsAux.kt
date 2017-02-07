package com.suhininalex.clones.core

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.suhininalex.clones.ide.document
import com.suhininalex.clones.ide.firstPsi
import java.util.*

fun splitToSiblings(clones: List<CloneClass>){
    val cloneClass = clones[0]
    val clone = cloneClass.clones.first()
    val tokens = cloneClass.tokenSequence().map { it.source.node.elementType }.toList()
    println(tokens)

    val end = clone.lastElement.source.textRange.endOffset
    var oldPsi = clone.firstPsi
    var newPsi = clone.firstPsi

    val result = ArrayList<Pair<PsiElement, PsiElement>>()

    while (newPsi.textRange.endOffset <= end) {
        val sibling = newPsi.nextSibling
        if (sibling != null) {
            if (sibling.textRange.endOffset > end) {
                result.add(oldPsi to newPsi)
                val child = sibling.findChildBeforeOffset(end) ?: break
                oldPsi = child
                newPsi = child
            } else {
                newPsi = sibling
            }
        } else {
            result.add(oldPsi to newPsi)
            val nextSibling = enshureSibling(newPsi.parent).nextSibling
            if (nextSibling.textRange.endOffset > end) {
                result.add(oldPsi to newPsi)
                val child = nextSibling.findChildBeforeOffset(end) ?: break
                oldPsi = child
                newPsi = child
            } else {
                oldPsi = nextSibling
                newPsi = nextSibling
            }
        }
    }

    result.filter { (a,b) -> getLength(a,b) > 50 }.forEach { (a, b) ->
        println("FROM: ${a.str} to ${b.str} LENGTH(${getLength(a, b)})")
        printRange(a, b)
    }
}

fun PsiElement.findChildBeforeOffset(offset: Int): PsiElement? {
    var current = this
    while (current.textRange.endOffset > offset)
        current = current.firstChild ?: return null;
    return current;
}

fun enshureSibling(psiElement: PsiElement): PsiElement {
    var current = psiElement
    while (current.nextSibling == null)
        current = current.parent
    return current;
}

val PsiElement.str: String
    get() = "${node.elementType}"

fun getLength(firstPsi: PsiElement, secondPsi: PsiElement): Int =
        secondPsi.textRange.endOffset - firstPsi.textRange.startOffset

fun printRange(firstPsi: PsiElement, secondPsi: PsiElement){
    val range = TextRange(firstPsi.textRange.startOffset, secondPsi.textRange.endOffset)
    println(firstPsi.document.getText(range))
}