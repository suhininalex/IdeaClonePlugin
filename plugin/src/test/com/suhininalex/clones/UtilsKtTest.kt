package com.suhininalex.clones

import com.mromanak.unionfind.UnionFindSet
import com.suhininalex.clones.core.utils.uniteRanges
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
    fun buildBadTree(){
        val tree = SuffixTree<Char>()
        tree.addSequence(listOf('a','x','a','x','a'))
        println(tree)
    }

    @Test
    fun unionSetTest(){
        val x = listOf(
            listOf(1,2,3),
            listOf(5,6,7),
            listOf(15),
            listOf(3,2,6,7,10)
        )
        val set = UnionFindSet(x.flatten())
        x.forEach {
            val one = it.first()
            it.forEach { set.join(it, one) }
        }
        set.equivalenceClasses.forEach(::println)
    }

    @Test
    fun uniteRangesTest(){
        val ranges = listOf(
                4..8,
                9..20,
                50..100
        )
        println(ranges.uniteRanges())
    }

    @Test
    fun uniteRangesTest2(){
        println(5.hashCode())
    }
}

fun Int.hashCode() = this + 1
