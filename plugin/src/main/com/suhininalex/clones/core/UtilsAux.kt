package com.suhininalex.clones.core

import com.intellij.openapi.util.TextRange
import com.intellij.psi.JavaTokenType
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.ElementType.*
import com.intellij.psi.tree.TokenSet
import com.suhininalex.clones.ide.document
import com.suhininalex.clones.ide.firstPsi

fun splitToSiblings(clones: List<CloneClass>){
    clones.flatMap { it.splitToSiblings() }.forEach {
        println("=========================================")
        it.forEach {
            println("---------------------")
            it.printText()
        }
    }
}

fun CloneClass.splitToSiblings(): List<List<Pair<PsiElement, PsiElement>>> {
    return clones.map { it.splitToSiblings() }.zipped()
}


/**
 * Only sequence of siblings is interesting as a clone
 */
fun Clone.splitToSiblings(): Sequence<Pair<PsiElement, PsiElement>> {
    val end = lastElement.source.textRange.endOffset
    var leftPsi: PsiElement? = firstPsi

    return generateSequence (firstPsi) { it.findNextSibling(end) }
            .filter { ! it.haveSibling(end) }
            .map {
                val result = leftPsi!! to it
                leftPsi = it.findNextSibling(end)
                result
            }
            .map { it.cropBadTokens() }
            .filter {it.getLength() > 50 }
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
        WHITE_SPACE, DOC_COMMENT, C_STYLE_COMMENT, END_OF_LINE_COMMENT, SEMICOLON, CODE_BLOCK, RPARENTH, LPARENTH, RBRACE, LBRACE,  EXPRESSION_LIST, COMMA
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

fun Pair<PsiElement, PsiElement>.getLength(): Int =
        second.textRange.endOffset - first.textRange.startOffset

fun Pair<PsiElement, PsiElement>.printText(){
    val range = TextRange(first.textRange.startOffset, second.textRange.endOffset)
    println(first.document.getText(range))
}

fun PsiElement.haveSibling(maxEndOffset: Int): Boolean {
    return nextSibling != null && nextSibling.textRange.endOffset <= maxEndOffset
}