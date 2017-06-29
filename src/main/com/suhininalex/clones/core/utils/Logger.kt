package com.suhininalex.clones.core.utils

import java.text.SimpleDateFormat
import java.util.*

object Logger {
    val enabled = false
    val time: String
        get() = SimpleDateFormat("hh:mm:ss").format(Date())
    fun log(message: String){
        if (enabled){
            println("[$time] $message")
        }
    }
}