package com.suhininalex.clones

import com.suhininalex.clones.core.times
import com.suhininalex.suffixtree.SuffixTree
import org.junit.Test

class UtilsKtTest {

    @Test
    fun atat(){
        val p = 3
        val q = arrayOf(1,2,3)
        q.asSequence()
        val x = generateSequence{ p }.take(1)
        println(x.toList())
    }

    @Test
    fun repeatStream(){
        val s = listOf(1,2,3)
        times(3) { s.asSequence() }.forEach { print("$it ") }
    }

    @Test
    fun buildBadTree(){
        val tree = SuffixTree<Char>()
        tree.addSequence(listOf('a','x','a','x','a'))
        println(tree)
    }
}