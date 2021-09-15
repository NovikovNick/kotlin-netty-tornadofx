package com.metalheart.model.dto

import com.metalheart.model.PlayerInput
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class InputDTO(val clientId: Long,
                    val srcFrame: Long,
                    val dstFrame: Long,
                    val input: PlayerInput) : DTO(InputDTO.javaClass.typeName) {

    override fun toByteArray(): ByteArray {
        return Json.encodeToString(this).toByteArray()
    }

    companion object {
        fun fromString(src: String): InputDTO {
            return Json.decodeFromString(src)
        }
    }
}

