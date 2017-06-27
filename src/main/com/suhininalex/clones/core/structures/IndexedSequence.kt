package com.suhininalex.clones.core.structures

interface IndexedSequence {
    /**
     * Sequence to be indexed
     */
    val sequence: Sequence<SourceToken>

    /**
     * Unique id for sequence based on path
     */
    val id: Int
}
