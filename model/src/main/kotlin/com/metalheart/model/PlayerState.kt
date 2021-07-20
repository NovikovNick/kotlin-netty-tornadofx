package com.metalheart.model

import java.time.Instant
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class PlayerState(private val capacity: Short) {

    private var input: Map<Long, PlayerInput> = HashMap()
    private var inputIds: Queue<Long> = ArrayDeque()

    private val lock: ReentrantLock = ReentrantLock()


    fun add(index: Long) {
        lock.withLock {
            if (inputIds.size >= capacity) {
                input -= inputIds.poll()
            }
            inputIds.add(index)
            input += index.to(PlayerInput(index, Instant.now(), null))
        }
    }

    fun confirm(index: Long) {
        lock.withLock {
            input[index]?.let {
                input += index.to(PlayerInput(it.sn, it.sentAt, Instant.now()))
            }
        }
    }

    fun forEach(action: (PlayerInput) -> Unit) {
        for ((id, received) in input) action(received)
    }
}