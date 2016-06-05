package com.suhininalex.clones

import com.suhininalex.clones.core.concat
import com.suhininalex.clones.core.times
import com.suhininalex.clones.core.zip
import com.suhininalex.suffixtree.SuffixTree
import org.junit.Test
import stream

class UtilsKtTest {

    @Test
    fun atat(){
        listOf(1,2,3,4,5).stream().concat(listOf(6,7).stream()).forEach { print("$it ") }
    }

    @Test
    fun repeatStream(){
        val s = listOf(1,2,3)
        times(3) { s.stream() }.forEach { print("$it ") }
    }

    @Test
    fun testZip(){
        val s1 = listOf(1,2,3,4)
        val s2 = listOf("first","second","third")
        zip(s1.stream(), s2.stream()).forEach { println(it) }
    }

    @Test
    fun buildBadTree(){
        val tree = SuffixTree<Char>()
        tree.addSequence(listOf('a','x','a','x','a'))
        println(tree)
    }
}