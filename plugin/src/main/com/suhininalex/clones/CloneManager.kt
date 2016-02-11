package com.suhininalex.clones

import addIf
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.psi.PsiMethod
import com.intellij.psi.impl.source.tree.ElementType
import com.intellij.psi.tree.TokenSet
import com.suhininalex.clones.clonefilter.*
import com.suhininalex.suffixtree.Node
import com.suhininalex.suffixtree.SuffixTree
import stream
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.stream.Stream
import kotlin.concurrent.read
import kotlin.concurrent.write

class CloneManager() {

    internal val methodIds: MutableMap<String, Long> = HashMap()
    internal val tree = SuffixTree<Token>()
    internal val rwLock = ReentrantReadWriteLock()
    internal val lengthClassFilter = LengthFilter(50)

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
        val sequence = method.body?.asStream(javaTokenFilter)?.map { node -> Token(node,method) }?.toList() ?: return
        val id = tree.addSequence(sequence)
        methodIds.put(method.getStringId(), id)
    }

    private fun removeMethodUnlocked(method: PsiMethod) {
        val id = methodIds[method.getStringId()] ?: return
        methodIds.remove(method.getStringId())
        tree.removeSequence(id)
    }

    private fun getAllCloneClasses()  =
        tree.root.depthFirstTraverse { it.edges.stream().map { it.terminal }.filter { it!=null } }
            .map { CloneClass(it) }
            .filter { lengthClassFilter.isAllowed(it) }


    fun filteredClonesApply(callback: (List<CloneClass>) -> Unit) =
        ProgressManager.getInstance().run(
            FilterTask(getAllCloneClasses().toList(), callback)
        )

    class FilterTask(val cloneClasses: List<CloneClass>, val success: (List<CloneClass>) -> Unit) : Task.Backgroundable(null, "Filtering...", true) {
            var filteredClones: List<CloneClass>? = null

            override fun run(progressIndicator: ProgressIndicator) {
                val commonFilter = createCommonFilter(cloneClasses)
                filteredClones = cloneClasses.stream()
                    .peekIndexed { i, cloneClass ->
                        if (progressIndicator.isCanceled) throw InterruptedException()
                        progressIndicator.fraction = i.toDouble()/cloneClasses.size
                    }
                    .filter { commonFilter.isAllowed(it) }.toList()
            }

            override fun onSuccess() {
                success(filteredClones!!)
            }
        }

    fun Stream<CloneClass>.applyFilters(): Stream<CloneClass> {
        val clones = this.toList()
        val commonFilter = createCommonFilter(clones)
        return clones.stream().filter { commonFilter.isAllowed(it)}
    }

    private fun getAllMethodClasses(method: PsiMethod): Stream<CloneClass> {
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
        return classes.stream()
    }

    fun PsiMethod.getId() = methodIds[getStringId()]
}

fun createCommonFilter(cloneClasses: List<CloneClass>): CloneClassFilter {
    val subclassFilter = SubclassFilter(cloneClasses)
    return CloneClassFilter { subclassFilter.isAllowed(it) && SubSequenceFilter.isAllowed(it)  } //&& CropTailFilter.isAllowed(it)
}