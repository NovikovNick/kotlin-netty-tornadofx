package com.metalheart.model.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class AcknowledgementDTO(val clientId: Long,
                              val dstFrame: Long) : DTO(AcknowledgementDTO.javaClass.typeName){

    override fun toByteArray(): ByteArray {
        return Json.encodeToString(this).toByteArray()
    }

    companion object {
        fun fromString(src: String): AcknowledgementDTO {
            return Json.decodeFromString(src)
        }
    }
}

