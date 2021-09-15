package com.metalheart.model.rollback

class LinkedRingBuffer<T>(private val capacity: Int) {

    private var head: Entity<T>? = null
    private var tail: Entity<T>? = null
    private var size: Int = 0

    fun forEach(action: (T) -> Unit) {

        var elem = head
        while (elem != null) {
            action(elem.data)
            elem = elem.prev
        }
    }

    fun remove(removed: T): Boolean {
        return when {
            size == 0 -> return false
            size == 1 -> { // remove last remaining element
                size = 0
                head = null
                tail = null
                return true
            }
            tail != null && tail!!.data == removed -> { // remove tail
                size--
                tail = tail!!.next
                tail!!.prev = null
                return true
            }
            head != null && head!!.data == removed -> { // remove head
                size--
                head = head!!.prev
                head!!.next = null
                return true
            }
            else -> { // remove from the middle
                size--
                var elem = tail
                while (elem != null) {

                    if (elem.data == removed) {
                        val prev = elem.prev!!
                        val next = elem.next!!

                        prev.next = next
                        next.prev = prev
                        return true
                    }
                    elem = elem.next
                }
                return false
            }
        }
    }

    fun add(elem: T) {
        when {
            head == null -> { // first element
                size++
                val element: Entity<T> = Entity(elem, null, null)
                head = element
                tail = element
            }
            head!!.prev == null -> { // second element
                size++
                head = Entity(elem, tail, null)
                tail!!.next = head
            }
            size < capacity -> { // doesn't reach limit of ring buffer
                size++
                val entity = Entity(elem, head, null)
                head!!.next = entity
                head = entity
            }
            else -> { // reach limit of ring buffer
                val entity = Entity(elem, head, null)
                head!!.next = entity
                head = entity
                tail = tail!!.next
                tail!!.prev = null
            }
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