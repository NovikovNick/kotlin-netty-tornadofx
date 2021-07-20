package com.metalheart.client.controller

import com.metalheart.client.Network
import com.metalheart.model.PlayerInput
import com.metalheart.model.PlayerState
import tornadofx.Controller
import java.time.Instant
import java.util.*
import kotlin.concurrent.schedule

class ClientController : Controller() {

    private lateinit var network: Network
    private lateinit var syncTask: TimerTask

    private val playerState = PlayerState(60)
    var sn: Long = 0

    fun connect() {
        network = Network(this)
        network.connect()
    }

    fun receive(snapshot: Long) {
        playerState.confirm(snapshot)
    }

    fun getState(): PlayerState {
        return playerState
    }

    fun stopSync() {
        syncTask.cancel()
    }

    fun startSync(tickRate: Int) {
        syncTask = Timer().schedule(0L, 1000L / tickRate) { sync() }
    }

    fun sync() {
        val input = PlayerInput(sn++, Instant.now(), null)
        playerState.add(input.sn)
        network.send(input)
    }
}
