package com.suhininalex.clones.core.structures

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.source.tree.ElementType
import com.intellij.psi.tree.TokenSet
import com.suhininalex.clones.core.utils.*

class SourceToken(source: PsiElement) : Comparable<SourceToken> {

    val file: VirtualFile = source.containingFile.virtualFile
    val offset: Int = source.textOffset
    val value: Int = calculateValue()

    val source: PsiElement
        get() {
            return PsiManager.getInstance(CurrentProject!!).findFile(file)!!.findElementAt(offset)!!
        }

    val nonAnonimized: Boolean
        get() {
            return source.notNeedAnonimization()
        }

    val isValid: Boolean
        get() {
            return file.isValid
        }

    fun calculateValue(): Int {
        return if (nonAnonimized) {
            source.text.hashCode()
        } else {
            source.node.elementType.index.toInt()
        }
    }

    override fun hashCode() = value.hashCode()

    override fun compareTo(other: SourceToken): Int {
        return value.compareTo(other.value)
    }

    override fun equals(other: Any?) =
        if (other is SourceToken) value == other.value
        else false

    override fun toString() =
        if (nonAnonimized) {
            source.text.abbreviate(5)
        } else {
            source.node.elementType.toString()
        }
}

fun PsiElement.notNeedAnonimization() =
    parent?.parent in TokenSet.create(ElementType.METHOD_CALL_EXPRESSION) &&
    parent in TokenSet.create(ElementType.REFERENCE_EXPRESSION) &&
    this in TokenSet.create(ElementType.IDENTIFIER)