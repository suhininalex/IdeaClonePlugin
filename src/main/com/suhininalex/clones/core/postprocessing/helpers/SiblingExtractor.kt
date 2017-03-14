package com.suhininalex.clones.core.postprocessing.helpers

import com.intellij.psi.PsiElement
import com.suhininalex.clones.core.structures.PsiRange
import com.suhininalex.clones.core.structures.RangeClone

/**
 * Only sequence of siblings is interesting as a clone
 */
fun PsiRange.extractSiblingSequences(): Sequence<PsiRange> {
    val maxEndOffset = lastPsi.textRange.endOffset
    var leftPsi: PsiElement? = firstPsi
    return generateSequence (firstPsi) { it.findMaxNextElement(maxEndOffset) }
            .filter { ! it.haveSibling(maxEndOffset) }
            .map {
                val result = RangeClone(leftPsi!!, it)
                leftPsi = it.findMaxNextElement(maxEndOffset)
                result
            }
}


private fun PsiElement.findMaxNextElement(maxEndOffset: Int): PsiElement? {
    return findParentWithSibling().nextSibling.findMaxChildBeforeOffset(maxEndOffset)
}

/**
 * Takes first child inclusive @this which ends before offset
 * returns null if there is no such element
 */
private fun PsiElement.findMaxChildBeforeOffset(offset: Int): PsiElement? {
    var current = this
    while (current.textRange.endOffset > offset) {
        current = current.firstChild ?: return null
    }
    return current
}

/**
 * Finds first parent inclusive @this which have nextSibling
 */
private fun PsiElement.findParentWithSibling(): PsiElement {
    var current = this
    while (current.nextSibling == null) {
        current = current.parent
    }
    return current
}


private fun PsiElement.haveSibling(maxEndOffset: Int): Boolean =
        nextSibling != null && nextSibling.before(maxEndOffset)

private fun PsiElement.before(endOffset: Int): Boolean =
        textRange.endOffset <= endOffset