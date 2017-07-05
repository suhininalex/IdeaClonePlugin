package com.suhininalex.clones.ide.toolwindow

import com.intellij.openapi.Disposable
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.suhininalex.clones.core.structures.Clone
import com.suhininalex.clones.core.utils.*
import java.awt.EventQueue

class ViewNodeInvalidator(val view: CloneTreeView) {

    private val sequenceClones: Map<VirtualFile, List<ViewClone>> =
            view.root.allNodes().filterIsInstance<ViewClone>().groupBy { it.clone.file }

    private fun invalidateByPsiElement(psiElement: PsiElement) {
        sequenceClones[psiElement.containingFile.virtualFile]
                ?.filter { ! it.clone.hasValidElements || it.clone.intersectsByStartOrEnd(psiElement)  }
                ?.forEach { invalidateView(it) }
    }

    private fun Clone.intersectsByStartOrEnd(psiElement: PsiElement): Boolean =
        firstPsi.textRange intersects psiElement.textRange || lastPsi.textRange intersects psiElement.textRange

    private fun invalidateByFile(virtualFile: VirtualFile) {
        sequenceClones[virtualFile]?.forEach { invalidateView(it) }
    }

    fun invalidateInvolvedCloneViews(element: PsiElement?) {
        Logger.log("[CloneViewInvalidator] Invalidating element $element")
        element ?: return
        if (element is PsiDirectory) {
            Logger.log("[CloneViewInvalidator] Invalidating directory ${element.name}")
            PsiTreeUtil.findChildrenOfType(element, PsiFile::class.java).forEach {
                Logger.log("[CloneViewInvalidator] Invalidating file ${it.name}")
                invalidateByFile(it.virtualFile)
            }
        } else {
            invalidateByPsiElement(element)
        }
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

    override fun beforeChildRemoval(event: PsiTreeChangeEvent) {
        invalidator.invalidateInvolvedCloneViews(event.child)
    }

}
