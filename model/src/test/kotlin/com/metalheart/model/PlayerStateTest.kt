package com.metalheart.model

import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class PlayerStateTest {

    @Test
    fun test() {

        val latch = CountDownLatch(2000)
        val playerState = PlayerState(5)

        for (i in 1..1000) {
            thread {
                playerState.add(i.toLong())
                playerState.forEach { println("add $i: $it") }
                println(" --- ")
                latch.countDown()
            }
        }

        for (i in 1..1000) {

            thread {
                playerState.confirm(i.toLong())
                playerState.forEach { println("con $i: $it") }
                println(" --- ")
                latch.countDown()
            }
        }
        latch.await(3, TimeUnit.SECONDS)
        assert(latch.count == 0L)
    }
}