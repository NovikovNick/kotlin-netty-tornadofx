package com.metalheart.model

import java.time.Instant

data class PlayerInput(
        val sn: Long,
        val sentAt: Instant,
        val ackAt: Instant?) {

    fun ack() : Boolean {
        return ackAt != null
    }
}
