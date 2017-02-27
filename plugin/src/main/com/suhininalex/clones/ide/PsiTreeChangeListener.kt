@file:Suppress("UNUSED_PARAMETER")

package com.suhininalex.clones.ide

import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.suhininalex.clones.core.CloneManager
import com.suhininalex.clones.core.utils.getCloneManager

class TreeChangeListener(val project: Project): PsiTreeChangeAdapter() {

    val cloneManager: CloneManager
        get() = project.getCloneManager()

    //TODO duplicated!
    override fun beforeChildAddition(event: PsiTreeChangeEvent) {
        detectInvolvedMethods(event.parent).forEach {
            cloneManager.removeMethod(it)
        }
    }

    override fun beforeChildReplacement(event: PsiTreeChangeEvent) {
        detectInvolvedMethods(event.parent).forEach {
            cloneManager.removeMethod(it)
        }
    }

    override fun beforeChildRemoval(event: PsiTreeChangeEvent) {
        detectInvolvedMethods(event.parent).forEach {
            cloneManager.removeMethod(it)
        }
    }

    override fun childRemoved(event: PsiTreeChangeEvent) {
        detectInvolvedMethods(event.parent).forEach {
            cloneManager.addMethod(it)
        }
    }

    override fun childAdded(event: PsiTreeChangeEvent) {
        detectInvolvedMethods(event.parent).forEach {
            cloneManager.addMethod(it)
        }
    }

    override fun childReplaced(event: PsiTreeChangeEvent) {
        detectInvolvedMethods(event.parent).forEach {
            cloneManager.addMethod(it)
        }
    }

}

fun detectInvolvedMethods(element: PsiElement): Collection<PsiMethod> =
    when (element) {
        is PsiMethod -> listOf(element)
        is PsiClass, is PsiJavaFile, is PsiDirectory -> element.childrenMethods
        else -> element.method?.let { listOf(it) } ?: emptyList()
    }

val PsiElement?.method: PsiMethod?
        get() =
            if (this is PsiMethod) {
                this
            } else {
                PsiTreeUtil.getParentOfType(this, PsiMethod::class.java)
            }

val PsiElement.childrenMethods: Collection<PsiMethod>
        get() = PsiTreeUtil.findChildrenOfType(this, PsiMethod::class.java)