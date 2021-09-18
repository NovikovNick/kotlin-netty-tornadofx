package com.metalheart.model.rollback.buffer

class LinkedRingBuffer<T : Comparable<T>>(private val capacity: Int) : SortedRingBuffer<T> {

    private var head: Entity<T>? = null
    private var tail: Entity<T>? = null
    private var size: Int = 0

    override fun forEach(action: (T) -> Unit) {

        var elem = tail
        while (elem != null) {
            action(elem.data)
            elem = elem.next
        }
    }

    override fun add(elem: T) {

        if (size == 0) {
            size++
            val element: Entity<T> = Entity(elem, null, null)
            head = element
            tail = element
            return
        }

        if (head!!.data < elem) {
            size++
            val element: Entity<T> = Entity(elem, head, null)
            head?.next = element
            head = element

        } else {

            var prev: Entity<T>? = null
            var cursor: Entity<T>? = head

            while (cursor != null) {

                if (cursor.data < elem) {

                    size++
                    val element: Entity<T> = Entity(elem, cursor, prev)
                    prev?.prev = element
                    cursor?.next = element
                    break

                } else if(cursor.prev == null) {

                    size++
                    val element = Entity(elem, null, tail)
                    tail?.prev = element
                    tail = element
                    break

                } else {
                    prev = cursor
                    cursor = cursor.prev;
                }
            }
        }

        if(size > capacity) {
            size--
            tail = tail!!.next
            tail?.prev = null
        }
    }

    private data class Entity<T>(val data: T,
                                 var prev: Entity<T>?,
                                 var next: Entity<T>?) {

        override fun toString(): String {
            return "$data[${prev?.data}; ${next?.data}]"
        }
    }
}