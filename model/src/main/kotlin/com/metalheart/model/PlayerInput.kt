package com.metalheart.model

import kotlinx.serialization.Serializable

@Serializable
data class PlayerInput(val frame: Long,
                       val m1Pressed: Boolean = false,
                       val wPressed: Boolean = false,
                       val aPressed: Boolean = false,
                       val sPressed: Boolean = false,
                       val dPressed: Boolean = false)
