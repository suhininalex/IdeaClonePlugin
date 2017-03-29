package com.suhininalex.clones.core.languagescope.kotlin

import com.intellij.psi.PsiElement
import com.suhininalex.clones.core.structures.IndexedSequence
import com.suhininalex.clones.core.structures.Token
import com.suhininalex.clones.core.utils.depthFirstTraverse
import com.suhininalex.clones.core.utils.isNoiseElement
import org.jetbrains.kotlin.cfg.pseudocode.containingDeclarationForPseudocode
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtPropertyAccessor

class KtIndexedPropertySequence(val ktPropertyAccessor: KtPropertyAccessor): IndexedSequence {

    override val sequence: Sequence<Token>
        get() = ktPropertyAccessor.toSequence().map(::Token)

    override val id: Int
        get() = System.identityHashCode(ktPropertyAccessor)
}

class KtIndexedFunSequence(val ktFunction: KtFunction): IndexedSequence {

    override val sequence: Sequence<Token>
        get() = ktFunction.bodyExpression?.toSequence()?.map(::Token) ?: emptySequence()

    override val id: Int
        get() = System.identityHashCode(ktFunction)
}

private fun PsiElement.toSequence(): Sequence<PsiElement> =
    depthFirstTraverse { it.psiChildren }.filter { it.firstChild == null }.filterNot(::isNoiseElement)

/**
 * PsiElement.children returns only KtElements
 * This property returns all psi children
 */
private val PsiElement.psiChildren: Sequence<PsiElement>
    get() = generateSequence(this.firstChild) {it.nextSibling}

private val KtElement.pathId: String
    get() = containingKtFile.packageFqName.asString() + "." +
            containingKtFile.name + "." +
            containingDeclarationForPseudocode?.name + ":"