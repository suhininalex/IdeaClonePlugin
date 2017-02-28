package com.suhininalex.clones.core.structures

class RangeCloneClass(val cloneRanges: List<Clone>): CloneClass {
    override val size: Int
        get() = cloneRanges.size

    override val clones: Sequence<Clone>
        get() = cloneRanges.asSequence()
}
