package com.suhininalex.clones.core.postprocessing

import com.intellij.openapi.progress.ProgressManager
import com.intellij.psi.PsiElement
import com.mromanak.unionfind.UnionFindSet
import com.suhininalex.clones.core.structures.Clone
import com.suhininalex.clones.core.structures.CloneClass
import com.suhininalex.clones.core.structures.RangeCloneClass
import com.suhininalex.clones.core.utils.*
import nl.komponents.kovenant.Promise
import java.lang.Exception

fun List<CloneClass>.mergeCloneClasses(): List<CloneClass> {
    val unionSet = UnionFindSet(this.flatMap { it.clones.toList() }.map(::CloneID))
    this.forEach {
        val first = CloneID(it.clones.first())
        it.clones.forEach { unionSet.join(first, CloneID(it)) }
    }
    return unionSet.equivalenceClasses.map { RangeCloneClass(it.map { it.clone }) }
}

fun ListWithProgressBar<CloneClass>.mergeCloneClasses(): Promise<List<CloneClass>, Exception> {
    return  ProgressManager.getInstance().backgroundTask(name){ progressIndicator ->
        val unionSet = UnionFindSet(list.flatMap { it.clones.toList() }.map(::CloneID))
        list.forEachIndexed { i, it ->
            if (progressIndicator.isCanceled) throw InterruptedException()
            progressIndicator.fraction = i.toDouble()/ list.size
            val first = CloneID(it.clones.first())
            it.clones.forEach { unionSet.join(first, CloneID(it)) }
        }
        unionSet.equivalenceClasses.map { RangeCloneClass(it.map { it.clone }) }
    }
}

private class CloneID(val clone: Clone){

    val left: PsiElement
        get() = clone.firstPsi

    val right: PsiElement
        get() = clone.lastPsi

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as CloneID

        if (left != other.left) return false
        if (right != other.right) return false

        return true
    }

    override fun hashCode(): Int {
        var result = left.hashCode()
        result = 31 * result + right.hashCode()
        return result
    }
}

