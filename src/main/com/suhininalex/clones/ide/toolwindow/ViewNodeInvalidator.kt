package com.suhininalex.clones.ide.toolwindow

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.suhininalex.clones.core.utils.*
import java.awt.EventQueue

class ViewNodeInvalidator(val view: CloneTreeView) {

    private val methodClones = view.root.allNodes().filterIsInstance<ViewClone>().groupBy { it.clone.firstPsi.method }

    fun invalidateClone(psiElement: PsiElement){
        methodClones[psiElement.method]
            ?.filter { it.clone.textRange intersects psiElement.textRange }
            ?.forEach{ invalidateView(it) }
    }

    fun invalidateMethod(psiMethod: PsiMethod){
        methodClones[psiMethod]?.forEach { invalidateView(it) }
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

    fun invalidateInvolvedCloneViews(psiElement: PsiElement){
        when (psiElement) {
            is PsiMethod -> invalidator.invalidateMethod(psiElement)
            is PsiDirectory, is PsiClass -> {
                psiElement.childrenMethods.forEach { invalidator.invalidateMethod(it) }
            }
            else -> invalidator.invalidateClone(psiElement)
        }
    }
}