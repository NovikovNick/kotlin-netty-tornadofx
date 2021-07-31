package com.metalheart.model.dto

import kotlinx.serialization.Serializable

@Serializable
abstract class DTO(val type: String) {

    abstract fun toByteArray(): ByteArray
}

