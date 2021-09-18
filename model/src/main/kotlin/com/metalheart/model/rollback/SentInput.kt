package com.metalheart.model.rollback

import com.metalheart.model.dto.ConfirmationDTO
import com.metalheart.model.dto.InputDTO
import com.metalheart.model.foo.ServerAck

interface SentInput {

    fun getInput(): List<InputDTO>
    fun receive(ack: ServerAck)
    fun getConfirm(): List<ConfirmationDTO>
}