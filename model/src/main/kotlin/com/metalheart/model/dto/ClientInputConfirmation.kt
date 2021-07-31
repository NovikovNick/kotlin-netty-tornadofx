package com.metalheart.model.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class ClientInputConfirmation(val clientId: Long,
                                   val confirmed: Set<Long>) : DTO(ClientInputConfirmation.javaClass.typeName){

    override fun toByteArray(): ByteArray {
        return Json.encodeToString(this).toByteArray()
    }

    companion object {
        fun fromString(src: String): ClientInputConfirmation {
            return Json.decodeFromString(src)
        }
    }
}
