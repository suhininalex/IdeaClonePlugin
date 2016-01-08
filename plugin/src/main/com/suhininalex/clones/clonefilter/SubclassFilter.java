package com.suhininalex.clones.clonefilter;

import com.suhininalex.clones.CloneClass;
import com.suhininalex.suffixtree.Node;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class SubclassFilter implements CloneClassFilter {

    /**
     * link from node to cloneClass with suffix link to this node
     */
    private final Map<Node, CloneClass> reverseSuffixLink = new IdentityHashMap<>();

    public SubclassFilter(Iterable<CloneClass> cloneClassesToFilter){
        for (CloneClass cloneClass : cloneClassesToFilter){
            Node node = cloneClass.getTreeNode();
            if (node.getSuffixLink()!=null)
                reverseSuffixLink.put(node.getSuffixLink(), cloneClass);
        }
    }

    @Override
    public boolean isAllowed(CloneClass cloneClass) {
        CloneClass greaterClass = reverseSuffixLink.get(cloneClass.getTreeNode());
        if (greaterClass==null) return true;
        else if (greaterClass.getSize()==cloneClass.getSize())
            return false;
        else return true;
    }
}
