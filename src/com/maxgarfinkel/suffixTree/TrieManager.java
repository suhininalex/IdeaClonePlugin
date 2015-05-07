package com.maxgarfinkel.suffixTree;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by llama on 25.02.15.
 */
public class TrieManager {
    public static <T,S extends Iterable<T>> List<Node<T,S>> followSuffixLink(@NotNull final Node<T,S> node, boolean excludeEmptyClones){
        List<Node<T,S>> nodes = new LinkedList<>();
        Node<T,S> currentNode = node;
        while (currentNode.getSuffixLink()!=null){
            if (excludeEmptyClones && currentNode.getCloneClass().isEmpty()) return nodes;
            nodes.add(currentNode);
            currentNode=currentNode.getSuffixLink();
        }
        nodes.add(currentNode);
        return nodes;
    }

    public static <T, S extends Iterable<T>> List<CloneClass> getAndFilterClones(@NotNull final SuffixTree<T, S> tree){
        List<CloneClass> clones = new LinkedList<>();
        Node<T,S> currentNode = findNodeForSequence(tree, tree.getSequence());
        while (currentNode.incomingEdge!=null){
            List<Node<T,S>> boundaryPath =  followSuffixLink(currentNode,true);
            if (!boundaryPath.isEmpty()) {
                clones.add(boundaryPath.get(0).cloneClass);
                for (int i = 1; i < boundaryPath.size(); i++) {
                    if (!boundaryPath.get(i - 1).cloneClass.canSimpleAbsorbe(boundaryPath.get(i).getCloneClass()))
                        clones.add(boundaryPath.get(i).getCloneClass());
                }
            }
            currentNode = currentNode.incomingEdge.parentNode;
        }
        return clones;
    }

    public static <T, S extends Iterable<T>> void markFiltered(@NotNull final Node<T,S> node){
        Node<T,S> current = node;
        if (node.getSuffixLink()!=null && node.getCloneClass().canSimpleAbsorbe(node.getSuffixLink().cloneClass))
            node.getSuffixLink().cloneClass.filtered = true;
        for (Edge<T, S> edge : node) {
            if (edge.isTerminating()) {
                markFiltered(edge.getTerminal());
            }
        }
    }

    public static void filterSomeClones(@NotNull final List<CloneClass> clones){
        Collections.sort(clones,CloneClass.getPositionComparator());
        int i=0;
        while (i < clones.size()-1){
            CloneClass first = clones.get(i);
            CloneClass second = clones.get(i+1);
            if (first.canAbsorbe(second)) {
                clones.remove(i+1);
                continue;
            }
            i++;
        }
    }

    public static void filterClonesBrute(@NotNull final List<CloneClass> clones){
        for (int i=0; i<clones.size();i++){
            CloneClass first = clones.get(i);
            for (int j=0; j<clones.size();j++){
                CloneClass second = clones.get(j);
                if (i!=j && first.canAbsorbe(second)) clones.remove(j--);
            }
        }
    }

    @NotNull
    public static <T,S extends Iterable<T>> List<CloneClass> getClones(@NotNull final SuffixTree<T,S> trie){
        List<CloneClass> result = new LinkedList<CloneClass>();
        getClonesFromNode(trie.getRoot(),result);
        return result;
    }

    private static <T,S extends Iterable<T>> void getClonesFromNode(Node<T,S> node, List<CloneClass> accumulator){
        if (!node.cloneClass.isEmpty() && !node.cloneClass.filtered) accumulator.add(node.cloneClass);
        for (Edge<T, S> edge : node) {
            if (edge.isTerminating()) {
                getClonesFromNode(edge.getTerminal(), accumulator);
            }
        }
    }

    public static <T, S extends Iterable<T>> Node<T,S> findNodeForSequence(SuffixTree tree, Iterable sequence){
        Iterator<Object> sequenceIt = sequence.iterator();
        Node<T, S> currentNode=tree.getRoot();
        if (!sequenceIt.hasNext()) return currentNode; //Пустая последовательность
        Object sequenceToken=sequenceIt.next();

        while(currentNode!=null){
            Edge<T,S> currentEdge = currentNode.getEdgeStarting(sequenceToken);
            if (currentEdge==null) return null;
            for (T treeToken : currentEdge) {
                if (sequenceToken.equals(treeToken))
                    if (sequenceIt.hasNext()) sequenceToken = sequenceIt.next();
                    else return currentNode;
                else return null;
            }
            currentNode = currentEdge.getTerminal();
        }
        return currentNode;
    }
}
