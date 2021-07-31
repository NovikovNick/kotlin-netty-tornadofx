package com.metalheart.model.state

import com.metalheart.model.PlayerInput

interface ClientInputView {
    fun getInputs() : Map<Long, List<PlayerInput>>
}