package com.suhininalex.clones.core.postprocessing

import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.vfs.VirtualFile
import com.mromanak.unionfind.UnionFindSet
import com.suhininalex.clones.core.structures.Clone
import com.suhininalex.clones.core.structures.CloneClass
import com.suhininalex.clones.core.structures.RangeCloneClass
import com.suhininalex.clones.core.utils.*
import nl.komponents.kovenant.Promise
import java.lang.Exception

fun List<CloneClass>.mergeCloneClasses(): List<CloneClass> {
    enshure { all{ it.clones.take(2).count() > 1} }
    val unionSet = UnionFindSet(this.flatMap { it.clones.toList() }.map(::CloneID))
    this.forEach {
        val first = CloneID(it.clones.first())
        it.clones.forEach { unionSet.join(first, CloneID(it)) }
    }
    return unionSet.equivalenceClasses.map { RangeCloneClass(it.map { it.clone }) }
}

fun ListWithProgressBar<CloneClass>.mergeCloneClasses(): Promise<List<CloneClass>, Exception> {
    enshure { list.all{ it.clones.take(2).count() > 1} }
    return  ProgressManager.getInstance().backgroundTask(name){ progressIndicator ->
        val unionSet = UnionFindSet(list.flatMap { it.clones.toList() }.map(::CloneID))
        list.forEachIndexed { i, it ->
            if (progressIndicator.isCanceled) throw InterruptedException()
            progressIndicator.fraction = i.toDouble()/ list.size
            val first = CloneID(it.clones.first())
            it.clones.drop(1).map { CloneID(it) }.forEach {
                unionSet.join(first, it)
            }
        }
        unionSet.equivalenceClasses.filter { it.size > 1 }.map { RangeCloneClass(it.map { it.clone }) }
    }
}

private class CloneID(val clone: Clone){

    val virtualFile: VirtualFile
        get(){
            return clone.file
        }

    val left: Int
        get() = clone.firstPsi.textRange.startOffset

    val right: Int
        get() = clone.lastPsi.textRange.endOffset

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as CloneID

        if (virtualFile != other.virtualFile) return false
        if (left != other.left) return false
        if (right != other.right) return false

        return true
    }

    override fun hashCode(): Int {
        var result = left.hashCode()
        result = 31 * result + right.hashCode()
        result = 31 * result + virtualFile.hashCode()
        return result
    }
}

