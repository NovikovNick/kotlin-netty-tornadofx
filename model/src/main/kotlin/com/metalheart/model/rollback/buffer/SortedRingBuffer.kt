package com.metalheart.model.rollback.buffer

interface SortedRingBuffer<T: Comparable<T>> {

    fun forEach(action: (T) -> Unit)

    fun add(elem: T)
}