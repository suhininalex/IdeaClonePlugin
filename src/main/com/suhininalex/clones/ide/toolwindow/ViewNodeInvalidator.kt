package com.suhininalex.clones.ide.toolwindow

import com.intellij.openapi.Disposable
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.suhininalex.clones.core.languagescope.LanguageIndexedPsiManager
import com.suhininalex.clones.core.structures.IndexedSequence
import com.suhininalex.clones.core.utils.*
import java.awt.EventQueue

class ViewNodeInvalidator(val view: CloneTreeView) {

    /**
     * Map IndexedSequence.id to ViewClone
     */
    private val sequenceClones: Map<String?, List<ViewClone>> =
            view.root.allNodes().filterIsInstance<ViewClone>().groupBy { it.clone.firstPsi.indexedSequence?.id }

    fun invalidateClone(psiElement: PsiElement){
        sequenceClones[psiElement.indexedSequence?.id]
            ?.filter { it.clone.textRange intersects psiElement.textRange }
            ?.forEach{ invalidateView(it) }
    }

    fun invalidateSequence(indexedSequence: IndexedSequence){
        sequenceClones[indexedSequence.id]?.forEach { invalidateView(it) }
    }

    private fun invalidateView(cloneView: ViewClone) {
        cloneView.invalidate()
        EventQueue.invokeLater { view.model.nodeChanged(cloneView) }
    }
}

infix fun TextRange.intersects(textRange: TextRange): Boolean =
    intersection(textRange) != null

class PsiTreeListener(val invalidator: ViewNodeInvalidator): PsiTreeChangeAdapter(), Disposable {

    init {
        PsiManager.getInstance(invalidator.view.project).addPsiTreeChangeListener(this)
    }

    override fun dispose() {
        PsiManager.getInstance(invalidator.view.project).removePsiTreeChangeListener(this)
    }

    override fun beforeChildReplacement(event: PsiTreeChangeEvent) {
        invalidateInvolvedCloneViews(event.oldChild)
    }

    override fun childAdded(event: PsiTreeChangeEvent) {
        invalidateInvolvedCloneViews(event.child)
    }

    override fun beforeChildMovement(event: PsiTreeChangeEvent) {
        invalidateInvolvedCloneViews(event.oldChild)
    }

    override fun beforeChildRemoval(event: PsiTreeChangeEvent) {
        invalidateInvolvedCloneViews(event.child)
    }

    fun invalidateInvolvedCloneViews(element: PsiElement?){
        element ?: return
        val indexedPsiDefiner = LanguageIndexedPsiManager.getIndexedPsiDefiner(element) ?: return
        return with (indexedPsiDefiner) {
            if (isIndexed(element)) {
                invalidator.invalidateSequence(indexedPsiDefiner.createIndexedSequence(element))
            } else if (isIndexedParent(element) || element is PsiDirectory || element is PsiFile) {
                getIndexedChildren(element).forEach { invalidator.invalidateSequence(indexedPsiDefiner.createIndexedSequence(it)) }
            } else {
                invalidator.invalidateClone(element)
            }
        }
    }
}

val PsiElement.indexedSequence: IndexedSequence?
    get() {
        val indexedPsiDefiner = LanguageIndexedPsiManager.getIndexedPsiDefiner(this) ?: return null
        val indexedElement = indexedPsiDefiner.getIndexedParent(this) ?: return null
        return indexedPsiDefiner.createIndexedSequence(indexedElement)
    }