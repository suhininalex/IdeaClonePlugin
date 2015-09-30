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

public class CloneManager {

    public final Map<String, Long> methodIds = new HashMap<>();

    final int minCloneLength = 70;

    public final SuffixTree<Token> tree = new SuffixTree<>();

    public synchronized void addMethod(PsiMethod method){
        TokenSet filter = TokenSet.create(ElementType.WHITE_SPACE,ElementType.SEMICOLON, ElementType.PARAMETER_LIST);
        long id = tree.addSequence(Utils.makeTokenSequence(method, filter));
        methodIds.put(Utils.getMethodId(method), id);
    }

    public synchronized void removeMethod(PsiMethod method){
        Long id = methodIds.get(Utils.getMethodId(method));
        if (id==null) return;
        methodIds.remove(method);
        tree.removeSequence(id);
    }

    public synchronized void updateMethod(PsiMethod method){
        removeMethod(method);
        addMethod(method);
    }

    public synchronized List<CloneClass> getAllClones(){
        List<CloneClass> list = new LinkedList<>();
        getAllClonesAUX(tree.getRoot(), list);
        return list;
    }

    private synchronized void getAllClonesAUX(Node node, List<CloneClass> accumulator){
        for (Edge edge : node.getEdges()){
            if (edge.getTerminal()!=null) {
                CloneClass cloneClass = new CloneClass(edge.getTerminal());
                if (!cloneClass.isEmpty() && cloneClass.getLength()>minCloneLength)
                    accumulator.add(cloneClass);
                getAllClonesAUX(edge.getTerminal(), accumulator);
            }
        }
    }

    public synchronized List<CloneClass> getAllFilteredClones(){
        List<CloneClass> allClones = getAllClones();

        CloneClassFilter subclassFilter = new SubclassFilter(allClones);
        List<CloneClass> filteredClones = new LinkedList<>();
        for (CloneClass cloneClass : allClones) {
            if (subclassFilter.isAllowed(cloneClass))
                filteredClones.add(cloneClass);
        }
        return filteredClones;
    }

    public synchronized List<CloneClass> getMethodFilteredClones(PsiMethod method){
        List<CloneClass> allClones = getAllMethodClones(method);

        CloneClassFilter subclassFilter = new SubclassFilter(allClones);
        List<CloneClass> filteredClones = new LinkedList<>();
        for (CloneClass cloneClass : allClones) {
            if (subclassFilter.isAllowed(cloneClass))
                filteredClones.add(cloneClass);
        }
        return filteredClones;
    }

    public synchronized List<CloneClass> getAllMethodClones(PsiMethod method){
        Map<Node, Boolean> visitedNodes = new IdentityHashMap<>();

        Long id = methodIds.get(Utils.getMethodId(method));
        if (id==null) {
            System.out.println("[SEVERE] " + methodIds.keySet());
            throw new IllegalStateException("There are no such method!");
        }
        List list = tree.getSequence(id);
//        System.out.println("Method: " + Utils.getMethodId(method)+list.size());
//        Node lastNode = tree.getLastSequenceNode(id);
//        CloneClass last = new CloneClass(lastNode);
//        System.out.println("Last node length: "+last.getLength());
//        System.out.println("tree: "+lastNode.subTreeToString());
//        System.out.println("id: "+id);
        List<CloneClass> clones = new LinkedList<>();

        for (Node branchNode : tree.getAllLastSequenceNodes(id)) {
            while (branchNode.getParentEdge() != null) {
                if (visitedNodes.getOrDefault(branchNode,false))
                    break;
                visitedNodes.put(branchNode, true);
                CloneClass cloneClass = new CloneClass(branchNode);
//                System.out.println("trying to add: " + cloneClass.isEmpty() + " | " + cloneClass.getLength());
                if (!cloneClass.isEmpty() && cloneClass.getLength() > minCloneLength) {
                    clones.add(cloneClass);
//                    System.out.println("success!");
                }
                branchNode = branchNode.getParentEdge().getParent();
            }
        }
        return clones;
    }
}
