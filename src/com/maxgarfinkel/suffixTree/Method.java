package com.maxgarfinkel.suffixTree;

import com.suhininalex.clones.Token;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Method {

    private final List<Token> tokens;

    public Method(List<Token> tokens) {
        this.tokens = tokens;
    }

    public int getId(){
        return Method.nextFreeId.getAndIncrement();
    };

    public List<Token> getTokens(){
        return Collections.unmodifiableList(tokens);
    };

    private static volatile AtomicInteger nextFreeId = new AtomicInteger(0);
}
