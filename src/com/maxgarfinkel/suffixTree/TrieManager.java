package com.maxgarfinkel.suffixTree;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by llama on 25.02.15.
 */
public class TrieManager {

    public static <T,S extends Iterable<T>> List<Node<T,S>> followSuffixLink(Node<T,S> node){
        List<Node<T,S>> nodes = new LinkedList<>();
        Node<T,S> currentNode = node;
        while (currentNode.getSuffixLink()!=null){
            nodes.add(currentNode);
            currentNode=currentNode.getSuffixLink();
        }
        return nodes;
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
        if (!node.cloneClass.isEmpty()) accumulator.add(node.cloneClass);
        for (Edge<T, S> edge : node) {
            if (edge.isTerminating()) {
                getClonesFromNode(edge.getTerminal(), accumulator);
            }
        }
    }

    public static <T, S extends Iterable<T>> boolean treeContainsSequence(SuffixTree<T,S> tree, S sequence){
        Iterator<T> sequenceIt = sequence.iterator();
        Node<T, S> currentNode=tree.getRoot();
        if (!sequenceIt.hasNext()) return true; //Пустая последовательность
        T sequenceToken=sequenceIt.next();

        while(currentNode!=null){
            Edge<T,S> currentEdge = currentNode.getEdgeStarting(sequenceToken);
            if (currentEdge==null) return false;
            for (T treeToken : currentEdge) {
                if (sequenceToken.equals(treeToken))
                    if (sequenceIt.hasNext()) sequenceToken = sequenceIt.next();
                    else return true;
                else return false;
            }
            currentNode = currentEdge.getTerminal();
        }
        return false;
    }

}
