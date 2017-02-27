package com.suhininalex.clones.core

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.ElementType.*
import com.intellij.psi.tree.TokenSet
import com.suhininalex.clones.core.structures.Clone
import com.suhininalex.clones.core.structures.CloneClass
import com.suhininalex.clones.core.utils.*

data class RangeClone(override val firstPsi: PsiElement, override val lastPsi: PsiElement): Clone

class RangeCloneClass(val cloneRanges: List<Clone>): CloneClass {
    override val size: Int
        get() = cloneRanges.size

    override val clones: Sequence<Clone>
        get() = cloneRanges.asSequence()
}

/**
 * Only sequence of siblings is interesting as a clone
 */
fun Clone.extractSiblingSequences(): Sequence<Clone> {
    val maxEndOffset = lastPsi.textRange.endOffset
    var leftPsi: PsiElement? = firstPsi
    //TODO fold
    return generateSequence (firstPsi) { it.findMaxNextElement(maxEndOffset) }
            .filter { ! it.haveSibling(maxEndOffset) }
            .map {
                val result = RangeClone(leftPsi!!, it)
                leftPsi = it.findMaxNextElement(maxEndOffset)
                result
            }
            .map { it.cropBadTokens() }
            .filter {it.tokenSequence().count() > 20 }
}

fun Clone.cropBadTokens(): Clone {
    var left = firstPsi
    var right = lastPsi
    while ( ! (left in lBraces && left.parent.lastChild.textRange.endOffset <= lastPsi.textRange.endOffset) && left in badTokens && left.nextSibling != null) {
        left = left.nextSibling
    }
    while (! (right in rBraces && right.parent.firstChild.textRange.startOffset >= firstPsi.textRange.startOffset) && right in badTokens && right.prevSibling != null) {
        right = right.prevSibling
    }
    return RangeClone(left, right)
}

val badTokens: TokenSet = TokenSet.create(
        WHITE_SPACE, DOC_COMMENT, C_STYLE_COMMENT, END_OF_LINE_COMMENT, SEMICOLON, RPARENTH, LPARENTH, RBRACE, LBRACE, EXPRESSION_LIST, COMMA
    )

val lBraces: TokenSet =
        TokenSet.create(LPARENTH, LBRACE, LBRACKET)

val rBraces: TokenSet =
        TokenSet.create(RPARENTH, RBRACE, RBRACKET)

fun PsiElement.findMaxNextElement(maxEndOffset: Int): PsiElement? {
    return findParentWithSibling().nextSibling.findMaxChildBeforeOffset(maxEndOffset)
}

/**
 * Takes first child inclusive @this which ends before offset
 * returns null if there is no such element
 */
fun PsiElement.findMaxChildBeforeOffset(offset: Int): PsiElement? {
    var current = this
    while (current.textRange.endOffset > offset) {
        current = current.firstChild ?: return null
    }
    return current
}

/**
 * Finds first parent inclusive @this which have nextSibling
 */
fun PsiElement.findParentWithSibling(): PsiElement {
    var current = this
    while (current.nextSibling == null) {
        current = current.parent
    }
    return current
}

fun Clone.getLength(): Int =
    lastPsi.textRange.endOffset - firstPsi.textRange.startOffset

fun PsiElement.haveSibling(maxEndOffset: Int): Boolean =
    nextSibling != null && nextSibling.before(maxEndOffset)

fun PsiElement.before(endOffset: Int): Boolean =
    textRange.endOffset <= endOffset

