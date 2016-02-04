package com.suhininalex.clones

import addIf
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.psi.PsiMethod
import com.intellij.psi.impl.source.tree.ElementType
import com.intellij.psi.tree.TokenSet
import com.suhininalex.clones.clonefilter.CloneClassFilter
import com.suhininalex.clones.clonefilter.LengthFilter
import com.suhininalex.clones.clonefilter.SubSequenceFilter
import com.suhininalex.clones.clonefilter.SubclassFilter
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
    internal val lengthClassFilter = LengthFilter(60)

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

    fun getAllFilteredClones() = rwLock.read {
        getAllCloneClasses().applyFilters()
    }

    fun getMethodFilteredClasses(method: PsiMethod) = rwLock.read {
        getAllMethodClasses(method).applyFilters()
    }

    val tokenFilter =
        TokenSet.create(ElementType.WHITE_SPACE, ElementType.SEMICOLON, ElementType.RBRACE, ElementType.LBRACE, ElementType.DOC_COMMENT, ElementType.C_STYLE_COMMENT, ElementType.END_OF_LINE_COMMENT, ElementType.ELSE_KEYWORD) //, ElementType.RPARENTH, ElementType.LPARENTH, ElementType.RBRACE, ElementType.LBRACE)

    private fun addMethodUnlocked(method: PsiMethod) {
        val sequence = method.body?.asStream(tokenFilter)?.map { node -> Token(node,method) }?.skip(1)?.toList() ?: return
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


    fun getAllWithProgress(): List<CloneClass> {
        val cloneClasses = getAllCloneClasses().toList()
        val progressManager = ProgressManager.getInstance()

        val task = {
            val commonFilter = createCommonFilter(cloneClasses)
            cloneClasses.stream().peekIndexed { i, cloneClass ->
                if (progressManager.progressIndicator.isCanceled) throw InterruptedException()
                progressManager.progressIndicator.fraction = i.toDouble()/cloneClasses.size
            }
            .filter { commonFilter.isAllowed(it) }.toList()
        }

        return progressManager.runProcessWithProgressSynchronously<List<CloneClass>, Exception>(task,"Filtering...", true, null)
    }

    fun createCommonFilter(cloneClasses: List<CloneClass>): CloneClassFilter {
        val subclassFilter = SubclassFilter(cloneClasses)
        return CloneClassFilter { subclassFilter.isAllowed(it) && SubSequenceFilter.isAllowed(it) }
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