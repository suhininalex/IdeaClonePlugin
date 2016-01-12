package com.suhininalex.clones

import clones.Utils
import com.intellij.psi.PsiMethod
import com.intellij.psi.impl.source.tree.ElementType
import com.intellij.psi.tree.TokenSet
import com.suhininalex.clones.clonefilter.LengthFilter
import com.suhininalex.clones.clonefilter.SubclassFilter
import com.suhininalex.suffixtree.Node
import com.suhininalex.suffixtree.SuffixTree
import stream
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock

class CloneManager(internal val minCloneLength: Int) {

    internal val methodIds: MutableMap<String, Long> = HashMap()
    internal val tree = SuffixTree<Token>()
    internal val rwLock = ReentrantReadWriteLock()
    internal val lengthClassFilter = LengthFilter(minCloneLength)


    fun addMethod(method: PsiMethod) = rwLock.writeLock() use {
        addMethodUnlocked(method)
    }

    fun removeMethod(method: PsiMethod) = rwLock.writeLock() use {
        removeMethodUnlocked(method)
    }

    fun updateMethod(method: PsiMethod) = rwLock.writeLock() use {
        removeMethodUnlocked(method)
        addMethodUnlocked(method)
    }

    fun getAllFilteredClones(): List<CloneClass> = rwLock.readLock() use {
        getFilteredClasses(getAllCloneClasses())
    }

    fun getMethodFilteredClasses(method: PsiMethod) = rwLock.readLock() use {
        getFilteredClasses(getAllMethodClasses(method))
     }

    private fun getTokenFilter() =
            TokenSet.create(ElementType.WHITE_SPACE, ElementType.SEMICOLON, ElementType.RBRACE, ElementType.LBRACE, ElementType.DOC_COMMENT, ElementType.C_STYLE_COMMENT)

    private fun addMethodUnlocked(method: PsiMethod) {
        val methodBody = method.body ?: return
        val id = tree.addSequence(Utils.makeTokenSequence(methodBody, getTokenFilter(), method))
        methodIds.put(method.getStringId(), id)
    }

    private fun removeMethodUnlocked(method: PsiMethod) {
        val id = methodIds[method.getStringId()] ?: throw IllegalArgumentException("There are no such method!")
        methodIds.remove(method.getStringId())
        tree.removeSequence(id)
    }

    private fun getAllCloneClasses(): LinkedList<CloneClass> {
        val list = LinkedList<CloneClass>()
        val stack = stack(tree.root)
        stack.popEach {
            it.edges.stream()
                .map { it.terminal }
                .filter { it != null }
                .forEach { terminal ->
                    stack.push(terminal)
                    list.addIf(CloneClass(terminal)) {lengthClassFilter.isAllowed(it)}
                }
        }
        return list
    }

    fun getFilteredClasses(cloneClasses: List<CloneClass>): List<CloneClass> {
        val subClassFilter = SubclassFilter(cloneClasses)
        return cloneClasses.filter { subClassFilter.isAllowed(it) }
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