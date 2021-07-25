package com.metalheart.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
class ClientInputData(val confirmed: Set<Long>,
                      val input: List<PlayerInput>) {
    fun toByteArray(): ByteArray {
        return Json.encodeToString(this).toByteArray()
    }

    companion object {
        fun fromString(src: String): ClientInputData {
            return Json.decodeFromString(src)
        }
    }
}
