@file:Suppress("UNUSED_PARAMETER")

package com.suhininalex.clones.ide

import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.suhininalex.clones.core.CloneIndexer
import com.suhininalex.clones.core.cloneManager
import com.suhininalex.clones.core.utils.childrenMethods
import com.suhininalex.clones.core.utils.method

class TreeChangeListener(val project: Project): PsiTreeChangeAdapter() {

    val cloneIndexer: CloneIndexer
        get() = project.cloneManager.instance

    override fun beforeChildAddition(event: PsiTreeChangeEvent) =
        removeInvolvedMethods(event)

    override fun beforeChildReplacement(event: PsiTreeChangeEvent) =
        removeInvolvedMethods(event)

    override fun beforeChildRemoval(event: PsiTreeChangeEvent) =
        removeInvolvedMethods(event)

    override fun childRemoved(event: PsiTreeChangeEvent) =
        addInvolvedMethods(event)

    override fun childAdded(event: PsiTreeChangeEvent) =
        addInvolvedMethods(event)

    override fun childReplaced(event: PsiTreeChangeEvent) =
        addInvolvedMethods(event)

    private fun addInvolvedMethods(event: PsiTreeChangeEvent){
        detectInvolvedMethods(event.parent).forEach {
            cloneIndexer.addMethod(it)
        }
    }

    private fun removeInvolvedMethods(event: PsiTreeChangeEvent) {
        detectInvolvedMethods(event.parent).forEach {
            cloneIndexer.removeMethod(it)
        }
    }
}

private fun detectInvolvedMethods(element: PsiElement): Collection<PsiMethod> =
    when (element) {
        is PsiMethod -> listOf(element)
        is PsiClass, is PsiJavaFile, is PsiDirectory -> element.childrenMethods
        else -> element.method?.let { listOf(it) } ?: emptyList()
    }