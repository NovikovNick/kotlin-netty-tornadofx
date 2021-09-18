package com.metalheart.model

import com.metalheart.model.rollback.buffer.LinkedRingBuffer
import com.metalheart.model.rollback.buffer.SetRingBuffer
import com.metalheart.model.rollback.buffer.SortedRingBuffer
import org.junit.jupiter.api.Test
import java.util.*

class SortedRingBufferTest {

    @Test
    fun testSetRingBuffer() {

        // arrange
        val ringBuffer: SortedRingBuffer<CustomDTO> = SetRingBuffer(5)

        // act
        ringBuffer.add(CustomDTO(1, "0"))
        ringBuffer.add(CustomDTO(0, "1"))
        ringBuffer.add(CustomDTO(3, "2"))
        ringBuffer.add(CustomDTO(2, "3"))
        ringBuffer.add(CustomDTO(5, "4"))
        ringBuffer.add(CustomDTO(4, "5"))
        ringBuffer.add(CustomDTO(6, "6"))

        // assert
        val list: MutableList<CustomDTO> = ArrayList()
        ringBuffer.forEach { list.add(it) }

        assert(list.size == 5 ) { "wrong size" }
        assert(list[0].frame == 2) { "wrong order" }
        assert(list[1].frame == 3) { "wrong order" }
        assert(list[2].frame == 4) { "wrong order" }
        assert(list[3].frame == 5) { "wrong order" }
        assert(list[4].frame == 6) { "wrong order" }
    }

    @Test
    fun testLinkedRingBuffer() {

        // arrange
        val ringBuffer: SortedRingBuffer<CustomDTO> = LinkedRingBuffer(5)

        // act
        ringBuffer.add(CustomDTO(1, "0"))
        ringBuffer.add(CustomDTO(0, "1"))
        ringBuffer.add(CustomDTO(3, "2"))
        ringBuffer.add(CustomDTO(2, "3"))
        ringBuffer.add(CustomDTO(5, "4"))
        ringBuffer.add(CustomDTO(4, "5"))
        ringBuffer.add(CustomDTO(6, "6"))

        // assert
        val list: MutableList<CustomDTO> = ArrayList()
        ringBuffer.forEach { list.add(it) }

        assert(list.size == 5 ) { "wrong size" }
        assert(list[0].frame == 2) { "wrong order" }
        assert(list[1].frame == 3) { "wrong order" }
        assert(list[2].frame == 4) { "wrong order" }
        assert(list[3].frame == 5) { "wrong order" }
        assert(list[4].frame == 6) { "wrong order" }
    }

    data class CustomDTO(val frame: Int, val payload: Any) : Comparable<CustomDTO> {
        override fun compareTo(other: CustomDTO): Int {
            return frame.compareTo(other.frame)
        }
    }
}

