package com.suhininalex.clones;

import Clones.Utils;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.tree.ElementType;
import com.intellij.psi.tree.TokenSet;
import com.suhininalex.clones.clonefilter.CloneClassFilter;
import com.suhininalex.clones.clonefilter.SubclassFilter;
import com.suhininalex.suffixtree.Edge;
import com.suhininalex.suffixtree.Node;
import com.suhininalex.suffixtree.SuffixTree;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class CloneManager {

    //TODO String id! srsly?
    final Map<String, Long> methodIds = new HashMap<>();
    final int minCloneLength;
    final SuffixTree<Token> tree = new SuffixTree<>();
    final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public CloneManager(int minCloneLength) {
        this.minCloneLength = minCloneLength;
    }

    //TODO try-with-res?
    public void addMethod(PsiMethod method){
        Lock lock = rwLock.writeLock();
        try {
            lock.lock();
            addMethodUnlocked(method);
        } finally {
            lock.unlock();
        }
    }

    public void removeMethod(PsiMethod method){
        Lock lock = rwLock.writeLock();
        try {
            lock.lock();
            removeMethodUnlocked(method);
        } finally {
            lock.unlock();
        }
    }

    public void updateMethod(PsiMethod method){
        Lock lock = rwLock.writeLock();
        try {
            lock.lock();
            removeMethodUnlocked(method);
            addMethodUnlocked(method);
        } finally {
            lock.unlock();
        }
    }

    public List<CloneClass> getAllFilteredClones(){
        Lock lock = rwLock.readLock();
        try {
            lock.lock();
            return getFilteredClones(getAllCloneClasses());
        } finally {
            lock.unlock();
        }
    }

    public List<CloneClass> getMethodFilteredClones(PsiMethod method){
        Lock lock = rwLock.readLock();
        try {
            lock.lock();
            return getFilteredClones(getAllMethodClones(method));
        } finally {
            lock.unlock();
        }
    }

    private void addMethodUnlocked(PsiMethod method){
        TokenSet filter = TokenSet.create(ElementType.WHITE_SPACE, ElementType.SEMICOLON, ElementType.RBRACE, ElementType.LBRACE, ElementType.DOC_COMMENT, ElementType.C_STYLE_COMMENT);
        if (method.getBody()==null) return;
        long id = tree.addSequence(Utils.makeTokenSequence(method.getBody(), filter));
        methodIds.put(Utils.getMethodId(method), id);
    }

    private void removeMethodUnlocked(PsiMethod method){
        Long id = methodIds.get(Utils.getMethodId(method));
        if (id==null) throw new IllegalArgumentException("There are no such method!");
        methodIds.remove(method);
        tree.removeSequence(id);
    }

    private List<CloneClass> getAllCloneClasses(){
        List<CloneClass> result = new LinkedList<>();
        Stack<Node> stack = new Stack<>();
        stack.push(tree.getRoot());

        while (!stack.isEmpty()){
            stack.pop().getEdges().stream()
                .map(Edge::getTerminal)
                .filter(terminal -> terminal != null)
                .forEach(terminal -> {
                    stack.push(terminal);
                    CloneClass cloneClass = new CloneClass(terminal);
                    if (!cloneClass.isEmpty() && cloneClass.getLength() > minCloneLength)
                        result.add(cloneClass);
                });
        }
        return result;

    }

    private List<CloneClass> getFilteredClones(List<CloneClass> cloneClasses){
        CloneClassFilter subclassFilter = new SubclassFilter(cloneClasses);
        return cloneClasses.stream()
                .filter(subclassFilter::isAllowed)
                .collect(Collectors.toList());
    }

    private List<CloneClass> getAllMethodClones(PsiMethod method){
        Map<Node, Boolean> visitedNodes = new IdentityHashMap<>();
        Long id = methodIds.get(Utils.getMethodId(method));
        if (id==null) {
            throw new IllegalStateException("There are no such method!");
        }
        List<CloneClass> clones = new LinkedList<>();
        for (Node branchNode : tree.getAllLastSequenceNodes(id)) {
            while (branchNode.getParentEdge()!=null && visitedNodes.get(branchNode)==null) {
                visitedNodes.put(branchNode, true);
                CloneClass cloneClass = new CloneClass(branchNode);
                if (!cloneClass.isEmpty() && cloneClass.getLength() > minCloneLength) {
                    clones.add(cloneClass);
                }
                branchNode = branchNode.getParentEdge().getParent();
            }
        }
        return clones;
    }
}
