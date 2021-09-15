package com.metalheart.model.rollback


class ArrayRingBuffer<T>(private val capacity: Byte) {

    val data : Array<T?> = arrayOfNulls<Any?>(capacity.toInt()) as Array<T?>
    private var cursor: Byte = 0

    fun forEach(action: (T) -> Unit) {

        var currentCursor = cursor

        for (i in 1..capacity) {
            currentCursor = prevOf(currentCursor, capacity)
            data[currentCursor.toInt()]?.let { action.invoke(it) }
        }
    }

    fun add(elem: T) {
        data[cursor.toInt()] = elem
        cursor = nextOf(cursor, capacity)
    }

    private fun nextOf(cursor: Byte, capacity: Byte): Byte = if (cursor + 1 >= capacity) 0 else (cursor + 1).toByte()
    private fun prevOf(cursor: Byte, capacity: Byte): Byte = if (cursor - 1 < 0)  (capacity - 1).toByte() else (cursor - 1).toByte()
}