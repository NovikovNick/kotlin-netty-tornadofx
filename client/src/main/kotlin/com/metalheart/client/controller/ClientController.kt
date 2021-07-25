package com.metalheart.client.controller

import com.metalheart.client.Network
import com.metalheart.model.ClientInputData
import com.metalheart.model.PlayerInput
import com.metalheart.model.PlayerInputBuffer
import tornadofx.Controller
import java.time.Instant
import java.util.*
import kotlin.concurrent.schedule

class ClientController : Controller() {

    private lateinit var network: Network
    private lateinit var syncTask: TimerTask

    private val playerState = PlayerInputBuffer(60)

    fun connect() {
        network = Network(this)
        network.connect()
    }

    fun receive(snapshot: Set<Long>) {
        playerState.confirm(snapshot, Instant.now().toEpochMilli())
    }

    fun getState(): PlayerInputBuffer {
        return playerState
    }

    fun stopSync() {
        syncTask.cancel()
    }

    fun startSync(tickRate: Int) {
        syncTask = Timer().schedule(0L, 1000L / tickRate) { sync() }
    }

    fun sync() {
        playerState.add(PlayerInput(Instant.now().toEpochMilli()))

        val input = ClientInputData(
                playerState.getConfirmedInputs().map { it.frame }.toSet(),
                playerState.getNotDeliveredInputs())

        network.send(input)
    }
}
