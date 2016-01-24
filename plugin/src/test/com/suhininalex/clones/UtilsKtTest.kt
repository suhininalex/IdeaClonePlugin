package com.suhininalex.clones

import org.junit.Test
import stream

class UtilsKtTest {

    @Test
    fun atat(){
        val s = listOf(1,2,3,4,5).stream().concat(listOf(6,7).stream())
    }
}