package com.metalheart.model

import com.metalheart.model.rollback.ArrayRingBuffer
import com.metalheart.model.rollback.LinkedRingBuffer
import org.junit.jupiter.api.Test

class RingBufferTest {

    @Test
    fun testLinkedRingBuffer() {

        // arrange
        val ringBuffer = LinkedRingBuffer<Int>(5)

        for (i in 1..100) {

            // act
            ringBuffer.add(i)

            // assert
            var prev: Int = -1
            ringBuffer.forEach {

                if (prev != -1) {
                    assert(it + 1 == prev) { "$it is not great then $prev" }
                }
                prev = it
            }
        }
    }

    @Test
    fun testArrayRingBuffer() {

        // arrange
        val ringBuffer = ArrayRingBuffer<Int>(5)

        for (i in 1..100) {

            // act
            ringBuffer.add(i)

            // assert
            var prev: Int = -1
            ringBuffer.forEach {

                if (prev != -1) {
                    assert(it + 1 == prev) { "$it is not great then $prev" }
                }
                prev = it
            }
        }
    }
}

