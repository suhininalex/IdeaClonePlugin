package com.suhininalex.clones.core

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.ElementType
import com.intellij.psi.impl.source.tree.JavaElementType
import com.intellij.psi.tree.TokenSet
import com.suhininalex.clones.core.interfaces.Clone
import com.suhininalex.clones.core.interfaces.CloneClass
import com.suhininalex.clones.ide.document

data class RangeClone(override val firstPsi: PsiElement, override val lastPsi: PsiElement): Clone

class RangeCloneClass(val cloneRanges: List<Clone>): CloneClass {
    override val clones: Sequence<Clone>
        get() = cloneRanges.asSequence()
}

fun List<CloneClass>.extractSiblingClones() : List<CloneClass> =
        flatMap ( CloneClass::extractSiblingClones )

fun CloneClass.extractSiblingClones(): List<CloneClass> =
        clones.map { it.normalize() }.map(Clone::extractSiblingSequences).zipped().map(::RangeCloneClass)

/**
 * Only sequence of siblings is interesting as a clone
 */
fun Clone.extractSiblingSequences(): Sequence<Clone> {
    val maxEndOffset = lastPsi.textRange.endOffset
    var leftPsi: PsiElement? = firstPsi
    //TODO fold
    return generateSequence (firstPsi) { it.findNextSibling(maxEndOffset) }
            .filter { ! it.haveSibling(maxEndOffset) }
            .map {
                val result = RangeClone(leftPsi!!, it)
                leftPsi = it.findNextSibling(maxEndOffset)
                result
            }
            .map { it.cropBadTokens() }
            .filter {it.tokenSequence().count() > 15 }
}

fun Clone.cropBadTokens(): Clone {
    var left = firstPsi
    var right = lastPsi
    while (left in badTokens && left.nextSibling != null) {
        left = left.nextSibling
    }
    while (right in badTokens && right.prevSibling != null) {
        right = right.prevSibling
    }
    return RangeClone(left, right)
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

fun PsiElement.normalizeUp(): PsiElement{
    var current = this
    while (current.textRange.startOffset == current.parent.textRange.startOffset) current = current.parent
    return current
}

fun Clone.getLength(): Int =
        lastPsi.textRange.endOffset - firstPsi.textRange.startOffset

fun Clone.printText(){
    val range = TextRange(firstPsi.textRange.startOffset, lastPsi.textRange.endOffset)
    println(firstPsi.document.getText(range))
}

fun PsiElement.haveSibling(maxEndOffset: Int): Boolean {
    return  nextSibling != null && nextSibling.before(maxEndOffset)
//            || parent.node.elementType == JavaElementType.REFERENCE_EXPRESSION && parent.haveSibling(maxEndOffset)
}

fun PsiElement.before(endOffset: Int): Boolean =
        textRange.endOffset <= endOffset
//                || node.elementType == JavaElementType.IF_STATEMENT && firstChild.before(endOffset)

