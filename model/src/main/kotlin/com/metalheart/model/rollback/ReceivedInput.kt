package com.metalheart.model.rollback

import com.metalheart.model.dto.ConfirmationDTO
import com.metalheart.model.dto.InputDTO
import com.metalheart.model.foo.ServerAck

interface ReceivedInput {

    fun receive(input: InputDTO)
    fun getAck(): List<ServerAck>
    fun receive(confirm: ConfirmationDTO)
}