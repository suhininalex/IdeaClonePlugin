package com.suhininalex.clones.core.utils

import java.lang.management.ManagementFactory

val enabledAssertions = ManagementFactory.getRuntimeMXBean().inputArguments.any { it.startsWith("-ea") }

inline fun enshure(condition: () -> Boolean){
    if (enabledAssertions){
        assert(condition())
    }
}