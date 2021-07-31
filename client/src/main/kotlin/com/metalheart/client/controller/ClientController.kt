package com.metalheart.client.controller

import com.metalheart.client.Network
import com.metalheart.model.PlayerInput
import com.metalheart.model.dto.ClientInput
import com.metalheart.model.dto.ClientInputConfirmation
import com.metalheart.model.state.ClientGameState
import tornadofx.Controller
import java.time.Instant
import java.util.*
import kotlin.concurrent.schedule

class ClientController : Controller() {

    private lateinit var network: Network
    private lateinit var syncTask: TimerTask

    private val clientState = ClientGameState(Random().nextLong())

    fun connect() {
        network = Network(this)
        network.connect()
    }

    fun receive(input: ClientInput) {
        clientState.update(input)
    }

    fun receive(input: ClientInputConfirmation) {
        clientState.update(input, Instant.now().toEpochMilli())
    }

    fun getState(): Map<Long, List<PlayerInput>> {
        return clientState.getInputs()
    }

    fun stopSync() {
        syncTask.cancel()
    }

    fun startSync(tickRate: Int) {
        syncTask = Timer().schedule(0L, 1000L / tickRate) { sync() }
    }

    fun sync() {

        clientState.add(PlayerInput(Instant.now().toEpochMilli()))

        val (input, confirm) = clientState.get()
        network.send(input)
        confirm.forEach { network.send(it) }
    }
}
