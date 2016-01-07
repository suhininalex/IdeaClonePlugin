package com.suhininalex.clones;

import com.suhininalex.suffixtree.Edge;
import com.suhininalex.suffixtree.Node;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * Pre-order traversal
 */
class CloneIterator implements Iterator<Clone> {

    private final Stack<CallInfo> stack = new Stack<>();
    private final int cloneLength;

    private void addNodeToStack(Node node, int depthOfsset){
        for (Edge edge : node.getEdges()) {
            int edgeLength = edge.getEnd() - edge.getBegin() + 1;
            stack.push(new CallInfo(edge, depthOfsset + edgeLength));
        }
    }

    public CloneIterator(Node node, int cloneLength){
        Node currentNode = node;

        this.cloneLength = cloneLength;

        if (cloneLength<=0)
            throw new IllegalArgumentException("Clone length migth be greater tan 0!");

        addNodeToStack(node, 0);
    }

    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    @Override
    public Clone next() {
        CallInfo callInfo = stack.pop();
        Edge edge = callInfo.edge;        int depthOffset = callInfo.depthOffset;

        if (edge.getTerminal()!=null) {
            addNodeToStack(edge.getTerminal(), depthOffset);
            return next();
        }

        int lastElementIndex = edge.getEnd() - depthOffset;
        List<Token> sequence = edge.getSequence();
        return new Clone(sequence.get(lastElementIndex - cloneLength + 1), sequence.get(lastElementIndex));
    }

    private static class CallInfo{
        final int depthOffset;
        final Edge edge;

        public CallInfo(Edge edge, int depthOffset) {
            this.depthOffset = depthOffset;
            this.edge = edge;
        }
    }
}
