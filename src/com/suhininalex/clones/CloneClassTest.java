package com.suhininalex.clones;

import com.suhininalex.suffixtree.Edge;
import com.suhininalex.suffixtree.Node;
import com.suhininalex.suffixtree.SuffixTree;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class CloneClassTest {


    @Test
    public void testGetClones(){
        System.out.println("-----------------------");
        System.out.println("testGetClones");
        SuffixTree<Token> tree = new SuffixTree<>();

        short[] tokens1 = {1,2,3,4};
        List<Token> sequence = new LinkedList<>();
        int pos=0;
        for (short token : tokens1){
            sequence.add(new Token(pos++, token));
        }

        short[] tokens2 = {1,2,7,9};
        List<Token> sequence2 = new LinkedList<>();
        pos=0;
        for (short token : tokens2){
            sequence2.add(new Token(pos++, token));
        }

        tree.addSequence(sequence);
        tree.addSequence(sequence2);

        System.out.println(sequence);
        System.out.println(sequence2);
        System.out.println(tree.toString());

        System.out.println("\n ---------------");

        Node node = null;
        for (Edge edge :tree.getRoot().getEdges()) {
            if (edge.getEnd()>0 && edge.getTerminal()!=null)
                node = edge.getTerminal();
        }

        CloneManager cm = new CloneManager();
        cm.tree = tree;

        for (CloneClass cloneClass : cm.getAllCloneClasses()){
            for (Clone clone : cloneClass.getClones()) {
                System.out.println(clone.firstElement.getPosition() + " | " + clone.lastElement.getPosition());
            }
        }
    }
}