//package com.suhininalex.clones;
//
//import com.suhininalex.clones.Token;
//import com.suhininalex.suffixtree.Edge;
//import com.suhininalex.suffixtree.Node;
//import com.suhininalex.suffixtree.SuffixTree;
//import org.junit.Test;
//
//import java.nio.file.Files;
//import java.util.LinkedList;
//import java.util.List;
//
//public class CloneClassTest {
//
//
//    @Test
//    public void testGetClones(){
//        Files.list("")
//
//        System.out.println("-----------------------");
//        System.out.println("testGetClones");
//        SuffixTree<Token> tree = new SuffixTree<>();
//
//        short[] tokens1 = {1,2,3,4};
//        List<Token> sequence = new LinkedList<>();
//        int pos=0;
//        for (short token : tokens1){
//            sequence.add(new Token(pos++, token));
//        }
//
//        short[] tokens2 = {1,2,7,9};
//        List<Token> sequence2 = new LinkedList<>();
//        pos=0;
//        for (short token : tokens2){
//            sequence2.add(new Token(pos++, token));
//        }
//
//        tree.addSequence(sequence);
//        tree.addSequence(sequence2);
//
//        System.out.println(sequence);
//        System.out.println(sequence2);
//        System.out.println(tree.toString());
//    }
//}