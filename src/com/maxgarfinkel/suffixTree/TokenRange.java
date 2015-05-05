package com.maxgarfinkel.suffixTree;

import org.jetbrains.annotations.NotNull;

public class TokenRange{
    public final Token begin;
    public final Token end;

    public TokenRange(@NotNull final Token begin, @NotNull final Token end) {
        this.begin = begin;
        this.end = end;
    }
}