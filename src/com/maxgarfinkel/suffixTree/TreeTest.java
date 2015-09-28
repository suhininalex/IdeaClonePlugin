package com.maxgarfinkel.suffixTree;

import com.suhininalex.clones.Clone;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;
import com.suhininalex.suffixtree.*;

import static junit.framework.Assert.*;
/**
 * Created by llama on 10.03.15.
 */
public class TreeTest {

    private int count = 5000;
    private boolean showInfo = false;
    private boolean showFails = true;


    private List<Token> getRandomSequence(){
        int[] tokens = {1,2,3,5,1,2,4,1,2,3,5,8,1,2,3,5,1,3};
        List<Token> sequence = new LinkedList<Token>();
        int pos=0;
        for (int token : tokens){
            sequence.add(new Token(token, pos++));
        }
        return sequence;
    }

    @Test
    public void testCloneFilter(){
        System.out.println("-----------------------");
        System.out.println("CloneClassFilter");
        List<Token> sequence = getRandomSequence();
        SuffixTree<Token, Collection<Token>> tree = new SuffixTree<Token, Collection<Token>>();
        tree.add(sequence);
        System.out.println(sequence);
        TrieManager.markFiltered(tree.getRoot());
        List<CloneClass> clones = TrieManager.getClones(tree);
        System.out.println(clones);
//        TrieManager.filterClonesBrute(clones);
//        System.out.println(clones);
//        System.out.println(tree);
    }

    @Test
    public void testRandomTree(){
        System.out.println("-----------------------");
        System.out.println("TestRandomTree");
        List<Token> sequence = getRandomSequence();
        SuffixTree<Token, Collection<Token>> tree = new SuffixTree<Token, Collection<Token>>();
        tree.add(sequence);
        System.out.println(sequence);
        System.out.println(tree);
    }

    @Test
    public void testDoubleAddition(){
        System.out.println("-----------------------");
        System.out.println("testDoubleAddition");
        SuffixTree<Token, Collection<Token>> tree = new SuffixTree<Token, Collection<Token>>();
        int[] tokens = {1,2,3,4};
        List<Token> sequence = new LinkedList<Token>();
        int pos=0;
        for (int token : tokens){
            sequence.add(new Token(token, pos++));
        }

        tree.add(sequence);
        List<Token> sequence2 = new LinkedList<Token>();
        sequence2.addAll(sequence);
        tree.add(sequence2);
        System.out.println(sequence2);
        System.out.println(tree);
    }

    @Test
    public void testContainsRandomSubString() throws IOException{
        List<Token> tokens = getTokensFromFile("sequence.txt");
        System.out.println(tokens);
        if (testSequence(tokens)) {
            System.out.println("OK");
        }
        else fail();
    }




    private boolean testSequence(List<Token> tokens){
        System.out.println("-------------------");
        System.out.println("Tree strucure test\n");

        Random randomizer = new Random();
        randomizer.setSeed(System.currentTimeMillis());
        SuffixTree<Token, Collection<Token>> tree = new SuffixTree<Token, Collection<Token>>();
        tree.add(tokens);

        for (int i=0;i<count;i++){
            int begin = randomizer.nextInt(tokens.size());
            int end = begin + randomizer.nextInt(tokens.size()-begin)+1;
            List<Token> subList =  tokens.subList(begin,end);
            ;
            if (TrieManager.findNodeForSequence(tree, subList)==null) {
                if (showFails) {
                    System.out.println("----------------------------");
                    System.out.println("Sequence not found!");
                    System.out.println("Source: " + tokens);
                    System.out.println("Sequence: " + subList);
                    System.out.println("Source tree:");
                    System.out.println(tree);
                }
                return false;
            }
            if (showInfo) System.out.println("Tested "+i+": substring"+subList);
        }
        System.out.println("Source tree:");
        System.out.println(tree);

        return true;
    }


    private List<Token> getTokensFromFile(String filename) throws IOException{
        Scanner scanner = new Scanner(new File("/home/llama/"+filename));
        List<Token> list = new LinkedList<Token>();
        int k=0;
        while (scanner.hasNextInt()){
            list.add(new Token(scanner.nextInt(),k++));
        }
        return list;
    }

    @Test
    public void testClones() throws IOException{
        System.out.println("-------------------");
        System.out.println("Clone test\n");
        List<Token> tokens = getTokensFromFile("sequence.txt");
        System.out.println(tokens);
        SuffixTree<Token, Collection<Token>> tree = new SuffixTree<Token, Collection<Token>>();
        tree.add(tokens);
        List<CloneClass> clones = TrieManager.getClones(tree);
        System.out.println("Before filtering " + clones);
//        TrieManager.filterClones(clones);
//        System.out.println("After " +clones);

    }


    
    public static class Token implements Comparable<Token> {
        public Integer value;

        /**
         * Position in code
         */
        public int position;

        public Token(int value, int position) {
            this.value = value;
            this.position = position;
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public  int compareTo(Token o){
            return value.compareTo(o.value);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Token)
                return value.equals(((Token) obj).value);
            else return false;
        }

        @Override
        public String toString() {
            return value.toString();
        }
    }


}