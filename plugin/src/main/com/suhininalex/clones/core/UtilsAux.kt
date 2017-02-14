package com.suhininalex.clones.core

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.ElementType.*
import com.intellij.psi.tree.TokenSet
import com.suhininalex.clones.ide.document
import com.suhininalex.clones.ide.endLine
import com.suhininalex.clones.ide.startLine
import java.util.*

data class CloneRange(val firstPsi: PsiElement, val lastPsi: PsiElement)

class CloneRangeClass(val cloneRanges: List<CloneRange>)

fun extractSiblingClones(clones: List<CloneClass>): List<CloneRangeClass> =
    clones.flatMap ( CloneClass::extractSiblingClones )

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

fun CloneRange.getLength(): Int =
        lastPsi.textRange.endOffset - firstPsi.textRange.startOffset

fun CloneRange.printText(){
    val range = TextRange(firstPsi.textRange.startOffset, lastPsi.textRange.endOffset)
    println(firstPsi.document.getText(range))
}

fun PsiElement.haveSibling(maxEndOffset: Int): Boolean {
    return nextSibling != null && nextSibling.textRange.endOffset <= maxEndOffset
}

class CloneRangeID(val cloneRange: CloneRange){

    val file = cloneRange.firstPsi.containingFile
    val startLine = cloneRange.firstPsi.startLine
    val endLine = cloneRange.lastPsi.endLine

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as CloneRangeID

        if (file != other.file) return false
        if (startLine != other.startLine) return false
        if (endLine != other.endLine) return false

        return true
    }

    override fun hashCode(): Int {
        var result = file?.hashCode() ?: 0
        result = 31 * result + startLine
        result = 31 * result + endLine
        return result
    }
}

fun filterSameCloneRangeClasses(clones: List<CloneRangeClass>): List<CloneRangeClass> {
    val map = HashMap<CloneRangeID, Int>()
    var groupId = 0
    clones.forEach { cloneRangeClass ->
        val cloneWithAnotherParent = cloneRangeClass.cloneRanges.find { map[CloneRangeID(it)] != null }
        val groupId: Int =
                if (cloneWithAnotherParent == null) {
                    groupId++
                } else {
                    map[CloneRangeID(cloneWithAnotherParent)]!!
                }

        cloneRangeClass.cloneRanges.map(::CloneRangeID).forEach {
            map.put(it, groupId)
        }
    }
    return map.entries.groupBy { it.value }.values.map { CloneRangeClass(it.map { it.key.cloneRange }) }
}