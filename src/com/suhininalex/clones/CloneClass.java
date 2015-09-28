package com.suhininalex.clones;

import com.suhininalex.suffixtree.Node;

import java.util.Comparator;


public class CloneClass {

    private final Node treeNode;
    private int size = -1;

    public CloneClass(Node treeNode) {
        this.treeNode = treeNode;
    }

    public Iterable<Clone> getClones(){
        return () -> new CloneIterator(treeNode);
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

    public int getLength(){
        return getClones().iterator().next().lastElement.getPosition() - getClones().iterator().next().firstElement.getPosition()+1;
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
}
