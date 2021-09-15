package com.metalheart.model.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class ConfirmationDTO(val clientId: Long,
                           val srcFrame: Long) : DTO(ConfirmationDTO.javaClass.typeName){

    override fun toByteArray(): ByteArray {
        return Json.encodeToString(this).toByteArray()
    }

    companion object {
        fun fromString(src: String): ConfirmationDTO {
            return Json.decodeFromString(src)
        }
    }
}

