package com.suhininalex.clones

import org.junit.Test
import stream
import java.util.stream.Stream
import kotlin.reflect.KFunction0

class UtilsKtTest {

    @Test
    fun atat(){
        listOf(1,2,3,4,5).stream().concat(listOf(6,7).stream()).forEach { print("$it ") }
    }

    @Test
    fun repeatStream(){
        val s = listOf(1,2,3)
        times(3){s.stream()}.forEach { print("$it ") }
    }

    @Test
    fun testZip(){
        val s1 = listOf(1,2,3)
        val s2 = listOf("first","second","third")
        zip(s1.stream(),s2.stream()).forEach { println(it) }
    }
}