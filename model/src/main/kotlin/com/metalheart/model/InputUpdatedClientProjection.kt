package com.metalheart.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
class InputUpdatedClientProjection(val confirmed: Set<Long>,
                                   val updated: Map<Long, Set<PlayerInput>>) {
    fun toByteArray(): ByteArray {
        return Json.encodeToString(this).toByteArray()
    }

    companion object {
        fun fromString(src: String): InputUpdatedClientProjection {
            return Json.decodeFromString(src)
        }
    }
}
