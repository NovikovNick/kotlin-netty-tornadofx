package com.metalheart.model.dto

import com.metalheart.model.PlayerInput
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class ClientInput(val clientId: Long,
                       val sn: Long,
                       val input: Set<PlayerInput>) : DTO(ClientInput.javaClass.typeName){

    override fun toByteArray(): ByteArray {
        return Json.encodeToString(this).toByteArray()
    }

    companion object {
        fun fromString(src: String): ClientInput {
            return Json.decodeFromString(src)
        }
    }
}

