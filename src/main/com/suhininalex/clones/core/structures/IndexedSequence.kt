package com.suhininalex.clones.core.structures

interface IndexedSequence {
    /**
     * Sequence to be indexed
     */
    val sequence: Sequence<Token>

    /**
     * Unique id for sequence based on path
     */
    val id: String
}
