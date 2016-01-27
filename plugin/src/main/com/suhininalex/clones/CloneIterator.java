//package com.suhininalex.clones;
//
//import com.suhininalex.suffixtree.Edge;
//import com.suhininalex.suffixtree.Node;
//
//import java.util.Iterator;
//import java.util.List;
//import java.util.Stack;
//
///**
// * Pre-order traversal
// */
//class CloneIterator implements Iterator<Clone> {
//
//    private final Stack<Edge> stack = new Stack<>();
//    private final int cloneLength;
//
//    private void addNodeToStack(Node node){
//        stack.addAll(node.getEdges());
//    }
//
//    public CloneIterator(Node node, int cloneLength){
//        this.cloneLength = cloneLength;
//        if (cloneLength>=0) addNodeToStack(node);
//    }
//
//    @Override
//    public boolean hasNext() {
//        return !stack.isEmpty();
//    }
//
//    @Override
//    public Clone next() {
//        Edge edge = stack.pop();
//
//        if (edge.getTerminal()!=null) {
//            addNodeToStack(edge.getTerminal());
//            return next();
//        }
//
//        int lastElementIndex = edge.getParent().getParentEdge().getEnd();
//        List<Token> sequence = edge.getSequence();
//        return new Clone(sequence.get(lastElementIndex - cloneLength + 1), sequence.get(lastElementIndex));
//    }
//}
