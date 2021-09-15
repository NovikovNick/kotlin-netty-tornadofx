package com.metalheart.model

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class PlayerInputBuffer(private val capacity: Short) {

    private var inputs: Set<PlayerInputWrapper> = HashSet()
    private var confirmed = java.util.HashSet<Long>()

    private val lock: ReentrantLock = ReentrantLock()


    fun add(input: PlayerInput) {
        lock.withLock {
            if (inputs.size >= capacity) {
                inputs -= inputs.first()
            }
            inputs += PlayerInputWrapper(input, input.frame, null)
        }
    }

    fun getNotDeliveredInputs(): List<PlayerInput> {
        return inputs
                .filter { it.confirmedAt == null }
                .map { it.input }
                .toList()
    }

    fun getConfirmedInputs(): Set<Long> {
        lock.withLock {
            val res = confirmed.toSet()
            confirmed.clear()
            return res
        }
    }

    fun getAllInputs(): List<PlayerInput> {
        return inputs.map { w -> w.input }.toList()
    }

    fun confirm(ack: Set<Long>, timestamp: Long) {

        lock.withLock {
            inputs = inputs
                    .map {
                        if (ack.contains(it.sn)){
                            confirmed.add(it.sn)
                            PlayerInputWrapper(it.input, it.sn, timestamp)
                        }
                        else it
                    }
                    .toSet()
        }
    }

    fun forEach(action: (PlayerInputWrapper) -> Unit): Unit {
        for (element in inputs) action(element)
    }

    data class PlayerInputWrapper(
            val input: PlayerInput,
            val sn: Long,
            val confirmedAt: Long?) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other?.javaClass != javaClass) return false

            other as PlayerInputWrapper

            if (sn != other.sn) return false

            return true
        }

        override fun hashCode(): Int {
            return sn.hashCode()
        }
    }
}