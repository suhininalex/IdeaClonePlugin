package com.suhininalex.clones;

import com.suhininalex.suffixtree.Edge;
import com.suhininalex.suffixtree.Node;
import com.suhininalex.suffixtree.SuffixTree;

import java.util.LinkedList;
import java.util.List;

public class CloneManager {

    final int minCloneLength = 30;

    public SuffixTree<Token> tree = new SuffixTree<>();

    public List<CloneClass> getAllCloneClasses(){
        List<CloneClass> list = new LinkedList<>();
        getAllCloneClassesAUX(tree.getRoot(), list);
        return list;
    }

    private void getAllCloneClassesAUX(Node node, List<CloneClass> accumulator){
        for (Edge edge : node.getEdges()){
            if (edge.getTerminal()!=null) {
                CloneClass cloneClass = new CloneClass(edge.getTerminal());
                if (!cloneClass.isEmpty() && cloneClass.getLength()>minCloneLength)
                    accumulator.add(cloneClass);
                getAllCloneClassesAUX(edge.getTerminal(), accumulator);
            }
        }
    }
}
