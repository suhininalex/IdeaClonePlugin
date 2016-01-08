package com.suhininalex.clones

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod

class Token(val source: PsiElement, val method: PsiMethod) : Comparable<Token> {

    val value: Short = source.node.elementType.index

    override fun hashCode() = value.hashCode()

    override fun compareTo(other: Token) = value.compareTo(other.value)

    override fun equals(other: Any?) =
        if (other is Token) value == other.value
        else false

    override fun toString() = value.toString()
}
