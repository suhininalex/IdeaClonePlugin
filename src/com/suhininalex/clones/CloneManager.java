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
import java.util.stream.Collectors;

public class CloneManager {

    final Map<String, Long> methodIds = new HashMap<>();
    final int minCloneLength;
    final SuffixTree<Token> tree = new SuffixTree<>();

    public CloneManager(int minCloneLength) {
        this.minCloneLength = minCloneLength;
    }

    public synchronized void addMethod(PsiMethod method){
        TokenSet filter = TokenSet.create(ElementType.WHITE_SPACE,ElementType.SEMICOLON, ElementType.PARAMETER_LIST);
        long id = tree.addSequence(Utils.makeTokenSequence(method, filter));
        methodIds.put(Utils.getMethodId(method), id);
    }

    public synchronized void removeMethod(PsiMethod method){
        Long id = methodIds.get(Utils.getMethodId(method));
        if (id==null) throw new IllegalArgumentException("There are no such method!");
        methodIds.remove(method);
        tree.removeSequence(id);
    }

    public synchronized void updateMethod(PsiMethod method){
        removeMethod(method);
        addMethod(method);
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

    public synchronized List<CloneClass> getAllFilteredClones(){
        return getFilteredClones(getAllCloneClasses());
    }

    public synchronized List<CloneClass> getMethodFilteredClones(PsiMethod method){
        return getFilteredClones(getAllMethodClones(method));
    }

    /**
     * @param cloneClasses will be corrupted!
     * @return filtered cloneClasses
     */
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
