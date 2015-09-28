package com.suhininalex.clones;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class Token implements Comparable<Token> {

    /**
     * Position in code
     */
    public PsiElement source;

    /**
     * Token id
     */
    //TODO finals
    public  Short value;
    private  Method method;
    private  int position;

    public int getPosition() {
        return position;
    }

    public Token(@NotNull final PsiElement source,@NotNull final Method method, int position) {
        this.source = source;
        this.position = position;
        this.method = method;
        value = source.getNode().getElementType().getIndex();
    }

    //TODO delete this
    public Token(int position, short value){
        this.position = position;
        this.value = value;
    }

    @NotNull
    public Method getMethod(){
        return method;
    }

    @NotNull
    Short getValue(){
        return value;
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }

    @Override
    public  int compareTo(Token o){
        return getValue().compareTo(o.getValue());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Token)
            return getValue().equals(((Token) obj).getValue());
        else return false;
    }

    @Override
    public String toString() {
        return getValue().toString();//source.getNode().getElementType().toString();
//        return "("+value + ":"+source.getText()+")";
    }
}
