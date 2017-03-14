package com.suhininalex.clones.core.structures

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.ElementType
import com.intellij.psi.tree.TokenSet
import com.suhininalex.clones.core.utils.abbreviate
import com.suhininalex.clones.core.utils.*

class Token(val source: PsiElement) : Comparable<Token> {

    val nonAnonimized = source.notNeedAnonimization()

    val value: Int =
        if (nonAnonimized)
            source.text.hashCode()
        else source.node.elementType.index.toInt()

    override fun hashCode() = value.hashCode()

    override fun compareTo(other: Token) = value.compareTo(other.value)

    override fun equals(other: Any?) =
        if (other is Token) value == other.value
        else false

    override fun toString() =
        if (nonAnonimized) {
            source.text.abbreviate(5)
        } else {
            source.node.elementType.toString()
        }
}

fun PsiElement.notNeedAnonimization() =
    parent.parent in TokenSet.create(ElementType.METHOD_CALL_EXPRESSION) &&
    parent in TokenSet.create(ElementType.REFERENCE_EXPRESSION) &&
    this in TokenSet.create(ElementType.IDENTIFIER)