package com.suhininalex.clones.core.languagescope.kotlin

import com.intellij.psi.PsiElement
import com.suhininalex.clones.core.structures.IndexedSequence
import com.suhininalex.clones.core.structures.SourceToken
import com.suhininalex.clones.core.utils.depthFirstTraverse
import com.suhininalex.clones.core.utils.isNoiseElement

class KtIndexedSequence(val psiElement: PsiElement): IndexedSequence {

    override val sequence: Sequence<SourceToken>
        get() = psiElement.toSequence().map(::SourceToken)

}

private fun PsiElement.toSequence(): Sequence<PsiElement> =
    lastChild.depthFirstTraverse { it.psiChildren }.filter { it.firstChild == null }.filterNot(::isNoiseElement)

/**
 * PsiElement.children returns only KtElements
 * This property returns all psi children
 */
private val PsiElement.psiChildren: Sequence<PsiElement>
    get() = generateSequence(this.firstChild) {it.nextSibling}
