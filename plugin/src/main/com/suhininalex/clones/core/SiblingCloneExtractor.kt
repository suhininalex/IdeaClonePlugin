package com.suhininalex.clones.core

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.ElementType
import com.intellij.psi.tree.TokenSet
import com.suhininalex.clones.ide.document

data class CloneRange(val firstPsi: PsiElement, val lastPsi: PsiElement)

class CloneRangeClass(val cloneRanges: List<CloneRange>)

fun List<CloneClass>.extractSiblingClones() : List<CloneRangeClass> =
        flatMap ( CloneClass::extractSiblingClones )

//fun extractSiblingClones(clones: List<CloneClass>): List<CloneRangeClass> =
//        clones.flatMap ( CloneClass::extractSiblingClones )

fun CloneClass.extractSiblingClones(): List<CloneRangeClass> =
        clones.map(Clone::extractSiblingSequences).zipped().map(::CloneRangeClass).filter { it.cloneRanges[0].getLength() > 50 }

/**
 * Only sequence of siblings is interesting as a clone
 */
fun Clone.extractSiblingSequences(): Sequence<CloneRange> {
    val maxEndOffset = lastElement.source.textRange.endOffset
    var leftPsi: PsiElement? = firstPsi
    //TODO fold
    return generateSequence (firstPsi) { it.findNextSibling(maxEndOffset) }
            .filter { ! it.haveSibling(maxEndOffset) }
            .map {
                val result = CloneRange(leftPsi!!, it)
                leftPsi = it.findNextSibling(maxEndOffset)
                result
            }
            .map { it.cropBadTokens() }
            .filter {it.getLength() > 0 }
}

fun CloneRange.cropBadTokens(): CloneRange {
    var left = firstPsi
    var right = lastPsi
    while (left in badTokens && left.nextSibling != null) {
        left = left.nextSibling
    }
    while (right in badTokens && right.prevSibling != null) {
        right = right.prevSibling
    }
    return CloneRange(left, right)
}

val badTokens = TokenSet.create(
        ElementType.WHITE_SPACE, ElementType.DOC_COMMENT, ElementType.C_STYLE_COMMENT, ElementType.END_OF_LINE_COMMENT, ElementType.SEMICOLON, ElementType.CODE_BLOCK, ElementType.RPARENTH, ElementType.LPARENTH, ElementType.RBRACE, ElementType.LBRACE, ElementType.EXPRESSION_LIST, ElementType.COMMA
)

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

fun CloneRange.getLength(): Int =
        lastPsi.textRange.endOffset - firstPsi.textRange.startOffset

fun CloneRange.printText(){
    val range = TextRange(firstPsi.textRange.startOffset, lastPsi.textRange.endOffset)
    println(firstPsi.document.getText(range))
}

fun PsiElement.haveSibling(maxEndOffset: Int): Boolean {
    return nextSibling != null && nextSibling.textRange.endOffset <= maxEndOffset
}
