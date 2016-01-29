package com.suhininalex.clones

import addIf
import com.intellij.psi.PsiMethod
import com.intellij.psi.impl.source.tree.ElementType
import com.intellij.psi.tree.TokenSet
import com.suhininalex.clones.clonefilter.LengthFilter
import com.suhininalex.clones.clonefilter.SubSequenceFilter
import com.suhininalex.clones.clonefilter.SubclassFilter
import com.suhininalex.suffixtree.Node
import com.suhininalex.suffixtree.SuffixTree
import stream
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class CloneManager(internal val minCloneLength: Int) {

    internal val methodIds: MutableMap<String, Long> = HashMap()
    internal val tree = SuffixTree<Token>()
    internal val rwLock = ReentrantReadWriteLock()
    internal val lengthClassFilter = LengthFilter(minCloneLength)

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
        getFilteredClasses(getAllCloneClasses())
    }

    //TODO clear me please
    fun getMethodFilteredClasses(method: PsiMethod) = rwLock.read {
        getFilteredClasses(getAllMethodClasses(method))
    }

    val tokenFilter =
        TokenSet.create(ElementType.WHITE_SPACE, ElementType.SEMICOLON, ElementType.RBRACE, ElementType.LBRACE, ElementType.DOC_COMMENT, ElementType.C_STYLE_COMMENT, ElementType.END_OF_LINE_COMMENT) //, ElementType.RPARENTH, ElementType.LPARENTH, ElementType.RBRACE, ElementType.LBRACE)

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

    private fun getAllCloneClasses() =
        tree.root.depthFirstTraverse { it.edges.stream().map { it.terminal }.filter { it!=null } }
            .map { CloneClass(it) }
            .filter { lengthClassFilter.isAllowed(it) }
            .toList()

    fun getFilteredClasses(cloneClasses: List<CloneClass>): List<CloneClass> {
        val subClassFilter = SubclassFilter(cloneClasses)
        return cloneClasses.stream().filter { subClassFilter.isAllowed(it) }.filter { SubSequenceFilter.isAllowed(it) }.toList()
    }

    private fun getAllMethodClasses(method: PsiMethod): List<CloneClass> {
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
        return classes
    }

    fun PsiMethod.getId() = methodIds[getStringId()]
}