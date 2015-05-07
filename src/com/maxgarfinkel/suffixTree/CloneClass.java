package com.maxgarfinkel.suffixTree;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by llama on 26.03.15.
 */
public class CloneClass {
    private final Set<Integer> startOfClones = new LinkedHashSet<Integer>();
    private final List sequence;
    private final int length;

    static private final int minCloneLength=1;

    public boolean filtered = false;

    CloneClass(@NotNull final List sequence, int length) {
        this.sequence = sequence;
        this.length = length;
    }

    @NotNull
    public int getLength(){
        return length;
    }

    @NotNull
    CloneClass getTrimmed(int endOffset){
        assert(endOffset>=0 && endOffset < getLength());
        CloneClass newClass = new CloneClass(sequence, length - endOffset);
        if (minCloneLength>newClass.length) return newClass;
        newClass.startOfClones.addAll(this.startOfClones);
        return newClass;
    }

    public boolean isEmpty(){
        return startOfClones.isEmpty();
    }

    /**
     * returns true if it succeeds
     */
    boolean addClone(int startPosition){
        if (minCloneLength>length) return false;
        assert(startPosition>=0);
        startOfClones.add(startPosition);
        return true;
    }

    @NotNull
    public List<TokenRange> getClones(){
        List<TokenRange> clones = new LinkedList<TokenRange>();
        for (int start : startOfClones) {
            clones.add(
                    new TokenRange(
                            (Token) sequence.get(start),
                            (Token) sequence.get(start+length-1)
                    )
            );
        }
        return clones;
    }

    @Override
    public String toString() {
        return "{"+startOfClones+","+getLength() + "}";
    }

    public boolean canAbsorbe(final @NotNull CloneClass cloneClass){
        if (this.length<cloneClass.length || this.startOfClones.size()<cloneClass.startOfClones.size()) return false;
        Iterator<Integer> startOfAnotherIt = cloneClass.startOfClones.iterator();
        int startOfAnother = startOfAnotherIt.next();
        for (int startOfThis : startOfClones){
            if (startOfThis<startOfAnother) {
                if (startOfThis + length>=startOfAnother + cloneClass.length) {
                    if (!startOfAnotherIt.hasNext()) return true;
                    startOfAnother = startOfAnotherIt.next();
                }
                else continue;
            } else return false;
        }
        return false;
    }

    public boolean canSimpleAbsorbe(final @NotNull CloneClass cloneClass){
        return this.startOfClones.size()==cloneClass.startOfClones.size();
    }

    /* a < b
    if first cloneStart a < first cloneStart b
    or if they are equal and a contains more fragments
     */
    public static Comparator<CloneClass> getPositionComparator(){
        return new Comparator<CloneClass>() {
            @Override
            public int compare(CloneClass o1, CloneClass o2) {
                if (o1.isEmpty() && o2.isEmpty()) return 0;
                else if (o1.isEmpty()) return -1;
                else if (o2.isEmpty()) return 1;
                int delta = o1.startOfClones.iterator().next() - o2.startOfClones.iterator().next();
                if (delta!=0) return delta;
                else return o2.startOfClones.size() - o1.startOfClones.size();
            }
        };
    }

    public static Comparator<CloneClass> getLengthComparator(){
        return new Comparator<CloneClass>() {
            @Override
            public int compare(CloneClass o1, CloneClass o2) {
                return o2.getLength() - o1.getLength();
            }
        };
    }
}

