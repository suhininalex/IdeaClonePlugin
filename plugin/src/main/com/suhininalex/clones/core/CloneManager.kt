package com.suhininalex.clones.core

import addIf
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.psi.PsiMethod
import com.suhininalex.clones.core.clonefilter.CloneClassFilter
import com.suhininalex.clones.core.clonefilter.LengthFilter
import com.suhininalex.clones.core.clonefilter.SubSequenceFilter
import com.suhininalex.clones.core.clonefilter.SubclassFilter
import com.suhininalex.suffixtree.Node
import com.suhininalex.suffixtree.SuffixTree
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class CloneManager() {

    internal val methodIds: MutableMap<String, Long> = HashMap()
    internal val tree = SuffixTree<Token>()
    internal val rwLock = ReentrantReadWriteLock()
    internal val lengthClassFilter = LengthFilter(40)

    fun addMethod(method: PsiMethod) = rwLock.write {
        addMethodUnlocked(method)
    }

    fun removeMethod(method: PsiMethod) = rwLock.write {
        removeMethodUnlocked(method)
    }

    fun updateMethod(method: PsiMethod) = rwLock.write {
        removeMethodUnlocked(method)
        addMethodUnlocked(method)
    }

    fun getMethodFilteredClasses(method: PsiMethod) = rwLock.read {
        getAllMethodClasses(method).applyFilters()
    }


    private fun addMethodUnlocked(method: PsiMethod) {
        val sequence = method.body?.asSequence()?.filter { it !in javaTokenFilter }?.map { node -> Token(node, method) }?.toList() ?: return
        val id = tree.addSequence(sequence)
        methodIds.put(method.getStringId(), id)
    }

    private fun removeMethodUnlocked(method: PsiMethod) {
        val id = methodIds[method.getStringId()] ?: return
        methodIds.remove(method.getStringId())
        tree.removeSequence(id)
    }

    private fun getAllCloneClasses(): Sequence<CloneClass>  =
        tree.root.depthFirstTraverse { it.edges.asSequence().map { it.terminal }.filter { it!=null } }
            .map { CloneClass(it) }
            .filter { lengthClassFilter.isAllowed(it) }


    fun filteredClonesApply(callback: (List<CloneClass>) -> Unit) =
        ProgressManager.getInstance().run(
            FilterTask(getAllCloneClasses().toList(), callback)
        )

    class FilterTask(val cloneClasses: List<CloneClass>, val success: (List<CloneClass>) -> Unit) : Task.Backgroundable(null, "Filtering...", true) {
            var filteredClones: List<CloneClass>? = null

            val filter by lazy {
                createCommonFilter(cloneClasses)
            }

            override fun run(progressIndicator: ProgressIndicator) {
                filteredClones = cloneClasses.filterIndexed { i, cloneClass ->
                        if (progressIndicator.isCanceled) throw InterruptedException()
                        progressIndicator.fraction = i.toDouble()/cloneClasses.size
                        filter.isAllowed(cloneClass)
                    }
            }

            override fun onSuccess() {
                success(filteredClones!!)
            }
        }

    fun Sequence<CloneClass>.applyFilters(): Sequence<CloneClass> {
        val clones = this.toList()
        val commonFilter = createCommonFilter(clones)
        return clones.asSequence().filter { commonFilter.isAllowed(it)}
    }

    fun getAllMethodClasses(method: PsiMethod): Sequence<CloneClass> {
        val classes = LinkedList<CloneClass>()
        val visitedNodes = HashSet<Node>()
        val id = method.getId() ?: throw IllegalStateException("There are no such method!")

        for (branchNode in tree.getAllLastSequenceNodes(id)) {
            for (currentNode in branchNode.riseTraverser()){
                if (visitedNodes.contains(currentNode)) break;
                visitedNodes.add(currentNode)
                classes.addIf(CloneClass(currentNode)) {lengthClassFilter.isAllowed(it)}
            }
        }
        return classes.asSequence()
    }

    fun PsiMethod.getId() = methodIds[getStringId()]
}

fun createCommonFilter(cloneClasses: List<CloneClass>): CloneClassFilter {
    val subclassFilter = SubclassFilter(cloneClasses)
    return CloneClassFilter { subclassFilter.isAllowed(it) && SubSequenceFilter.isAllowed(it) } //&& CropTailFilter.isAllowed(it)
}