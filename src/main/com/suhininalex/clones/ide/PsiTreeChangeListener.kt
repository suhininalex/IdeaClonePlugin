@file:Suppress("UNUSED_PARAMETER")

package com.suhininalex.clones.ide

import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.suhininalex.clones.core.CloneIndexer
import com.suhininalex.clones.core.cloneManager
import com.suhininalex.clones.core.languagescope.LanguageIndexedPsiManager
import com.suhininalex.clones.core.languagescope.java.JavaIndexedSequence
import com.suhininalex.clones.core.structures.IndexedSequence

class TreeChangeListener(val project: Project): PsiTreeChangeAdapter() {

    val cloneIndexer: CloneIndexer
        get() = project.cloneManager.instance

    override fun beforeChildAddition(event: PsiTreeChangeEvent) =
        removeInvolvedSequences(event)

    override fun beforeChildReplacement(event: PsiTreeChangeEvent) =
        removeInvolvedSequences(event)

    override fun beforeChildRemoval(event: PsiTreeChangeEvent) =
        removeInvolvedSequences(event)

    override fun childRemoved(event: PsiTreeChangeEvent) =
        addInvolvedSequences(event)

    override fun childAdded(event: PsiTreeChangeEvent) =
        addInvolvedSequences(event)

    override fun childReplaced(event: PsiTreeChangeEvent) =
        addInvolvedSequences(event)

    private fun addInvolvedSequences(event: PsiTreeChangeEvent){
        findInvolvedIndexedSequences(event.parent).forEach {
            cloneIndexer.addSequence(it)
        }
    }

    private fun removeInvolvedSequences(event: PsiTreeChangeEvent) {
        findInvolvedIndexedSequences(event.parent).forEach {
            cloneIndexer.removeSequence(it)
        }
    }
}

fun findInvolvedIndexedSequences(element: PsiElement): List<IndexedSequence> {
    val indexedPsiDefiner = LanguageIndexedPsiManager.getIndexedPsiDefiner(element) ?: return emptyList()
    return with (indexedPsiDefiner) {
        if (isIndexed(element)) {
            listOf(indexedPsiDefiner.createIndexedSequence(element))
        } else if (isIndexedParent(element) || element is PsiDirectory || element is PsiFile) {
            getIndexedChildren(element).map{ indexedPsiDefiner.createIndexedSequence(it)}
        } else {
            getIndexedParent(element)?.let { listOf(it).map{ indexedPsiDefiner.createIndexedSequence(it) } } ?: emptyList()
        }
    }
}