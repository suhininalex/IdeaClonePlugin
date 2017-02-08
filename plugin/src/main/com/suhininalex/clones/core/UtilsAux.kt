package com.suhininalex.clones.core

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.ElementType.*
import com.intellij.psi.tree.TokenSet
import com.suhininalex.clones.ide.document
import com.suhininalex.clones.ide.firstPsi

fun splitToSiblings(clones: List<CloneClass>){
    val cloneClass = clones[0]
    val clone = cloneClass.clones.first()
    val tokens = cloneClass.tokenSequence().map { it.source.node.elementType }.toList()
    println(tokens)

    val end = clone.lastElement.source.textRange.endOffset

    var leftPsi: PsiElement? = clone.firstPsi
    val result = generateSequence (clone.firstPsi) { it.findNextSibling(end) }
            .filter { ! it.haveSibling(end) }
            .map {
                val result = leftPsi!! to it
                leftPsi = it.findNextSibling(end)
                result
            }.toList()


    result
        .map {
            it.cropBadTokens()
        }
        .filter { (a,b) -> getLength(a,b) > 50 }
        .forEach { (leftPsi, rightPsi) ->
            println("FROM: ${leftPsi.str} to ${rightPsi.str} LENGTH(${getLength(leftPsi, rightPsi)})")
            printRange(leftPsi, rightPsi)
        }
}

fun Pair<PsiElement, PsiElement>.cropBadTokens(): Pair<PsiElement, PsiElement> {
    var (left, right) = this
    while (left in badTokens && left.nextSibling != null) {
        left = left.nextSibling
    }
    while (right in badTokens && right.prevSibling != null) {
        right = right.prevSibling
    }
    return left to right
}

val badTokens = TokenSet.create(
        WHITE_SPACE, DOC_COMMENT, C_STYLE_COMMENT, END_OF_LINE_COMMENT, SEMICOLON, CODE_BLOCK, RPARENTH, LPARENTH, RBRACE, LBRACE,  EXPRESSION_LIST
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

val PsiElement.str: String
    get() = "${node.elementType}"

fun getLength(firstPsi: PsiElement, secondPsi: PsiElement): Int =
        secondPsi.textRange.endOffset - firstPsi.textRange.startOffset

fun printRange(firstPsi: PsiElement, secondPsi: PsiElement){
    val range = TextRange(firstPsi.textRange.startOffset, secondPsi.textRange.endOffset)
    println(firstPsi.document.getText(range))
}

fun PsiElement.haveSibling(maxEndOffset: Int): Boolean {
    return nextSibling != null && nextSibling.textRange.endOffset <= maxEndOffset
}