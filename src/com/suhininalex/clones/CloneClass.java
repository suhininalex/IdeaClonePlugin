package com.suhininalex.clones;

import com.suhininalex.suffixtree.Edge;
import com.suhininalex.suffixtree.Node;

import java.util.Comparator;

/**
 * Объект теряет целостность при изменении дерева!
 * (Кэш длины может быть неверным, доступ к клонам корректный!)
 */
public class CloneClass {

    private final Node treeNode;
    private int size = -1;
    private final int length;

    public CloneClass(Node treeNode) {
        this.treeNode = treeNode;
        length = computeLengthToRoot(treeNode);
    }

    public Iterable<Clone> getClones(){
        return () -> new CloneIterator(treeNode, getLength());
    }

    public int size(){
        if (size<0) {
            int lastSize = 0;
            for (Clone clone : getClones()){
                lastSize++;
            }
            size = lastSize;
        }
        return size;
    }

    public boolean isEmpty(){
        return !getClones().iterator().hasNext();
    }

    public int getLength() {
        return length;
    }

    public Node getTreeNode(){
        return treeNode;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public static Comparator<CloneClass> getLengthComparator(){
        return (CloneClass first, CloneClass second)  -> first.getLength() - second.getLength();
    }

    private int computeLengthToRoot(Node node){
        int cloneLength = 0;
        while (node.getParentEdge()!=null){
            Edge edge = node.getParentEdge();
            int edgeLength = edge.getEnd() - edge.getBegin() + 1;
            cloneLength+=edgeLength;
            node = edge.getParent();
        }
        return cloneLength;
    }
}
