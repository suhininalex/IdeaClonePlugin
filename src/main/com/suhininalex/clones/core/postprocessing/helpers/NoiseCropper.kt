package com.suhininalex.clones.core.postprocessing.helpers

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.ElementType
import com.intellij.psi.tree.TokenSet
import com.suhininalex.clones.core.structures.Clone
import com.suhininalex.clones.core.structures.PsiRange
import com.suhininalex.clones.core.structures.RangeClone
import com.suhininalex.clones.core.utils.contains

fun PsiRange.cropBadTokens(): Clone {
    var left = firstPsi
    var right = lastPsi
    while (isUnclosedLeftBrace(left) || left in badTokens || left in rBraces) {
        left = left.nextSibling ?: break
    }
    while (isUnclosedRightBrace(right) || right in badTokens) {
        right = right.prevSibling ?: break
    }
    return if (left.textRange.startOffset < right.textRange.startOffset){
        RangeClone(left, right)
    } else {
        RangeClone(firstPsi, lastPsi)
    }
}

private fun PsiRange.isUnclosedLeftBrace(psiElement: PsiElement): Boolean =
        psiElement in lBraces && psiElement.parent.lastChild.textRange.endOffset > lastPsi.textRange.endOffset

private fun PsiRange.isUnclosedRightBrace(psiElement: PsiElement): Boolean =
        psiElement in rBraces && psiElement.parent.firstChild.textRange.startOffset < firstPsi.textRange.startOffset

private val badTokens: TokenSet = TokenSet.create(
        ElementType.WHITE_SPACE, ElementType.DOC_COMMENT, ElementType.C_STYLE_COMMENT, ElementType.END_OF_LINE_COMMENT, ElementType.SEMICOLON, ElementType.EXPRESSION_LIST, ElementType.COMMA
)

private val lBraces: Set<String> =
        setOf("LPARENTH", "LBRACE", "LBRACKET", "LPAR", "RBRACKET")

private val rBraces: Set<String> =
        setOf("RPARENTH", "RBRACE", "RBRACKET", "RPAR", "RBRACKET")

private operator fun Set<String>.contains(psiElement: PsiElement) =
        psiElement.node.elementType.toString() in this
