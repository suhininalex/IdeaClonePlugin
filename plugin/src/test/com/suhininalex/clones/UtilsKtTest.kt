package com.suhininalex.clones

import com.intellij.openapi.util.TextRange
import com.suhininalex.clones.core.times
import com.suhininalex.clones.core.uniteRanges
import com.suhininalex.suffixtree.SuffixTree
import org.junit.Test
import java.util.*

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
    fun buildBadTree(){
        val tree = SuffixTree<Char>()
        tree.addSequence(listOf('a','x','a','x','a'))
        println(tree)
    }

    @Test
    fun uniteRangesTest(){
        val ranges = listOf(
                TextRange(4, 8),
                TextRange(9, 20),
                TextRange(50, 100)
        )
        println(ranges.uniteRanges())
    }
}