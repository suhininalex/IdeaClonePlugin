package com.suhininalex.clones.core.postprocessing.helpers

import com.intellij.psi.impl.source.tree.ElementType
import com.intellij.psi.tree.TokenSet
import com.suhininalex.clones.core.structures.Clone
import com.suhininalex.clones.core.structures.PsiRange
import com.suhininalex.clones.core.structures.RangeClone
import com.suhininalex.clones.core.utils.contains

fun PsiRange.cropBadTokens(): Clone {
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

private val badTokens: TokenSet = TokenSet.create(
        ElementType.WHITE_SPACE, ElementType.DOC_COMMENT, ElementType.C_STYLE_COMMENT, ElementType.END_OF_LINE_COMMENT, ElementType.SEMICOLON, ElementType.RPARENTH, ElementType.LPARENTH, ElementType.RBRACE, ElementType.LBRACE, ElementType.EXPRESSION_LIST, ElementType.COMMA
)

private val lBraces: TokenSet =
        TokenSet.create(ElementType.LPARENTH, ElementType.LBRACE, ElementType.LBRACKET)

private val rBraces: TokenSet =
        TokenSet.create(ElementType.RPARENTH, ElementType.RBRACE, ElementType.RBRACKET)
