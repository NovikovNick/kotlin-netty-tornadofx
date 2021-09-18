package com.metalheart.model.rollback.buffer

import java.util.*

class SetRingBuffer<T: Comparable<T>>(private val capacity: Byte) : SortedRingBuffer<T> {

    val data : MutableSet<T> = TreeSet()

    override fun forEach(action: (T) -> Unit) {
        data.forEach(action)
    }

    override fun add(elem: T) {
        if (data.size >= capacity) {
            data.remove(data.first())
        }
        data.add(elem)
    }
}