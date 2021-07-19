package com.metalheart.client.controller

import com.metalheart.client.Network
import com.metalheart.client.model.PlayerState
import com.metalheart.model.PlayerInput
import com.metalheart.model.Point
import tornadofx.Controller
import java.time.Instant
import java.util.*
import kotlin.concurrent.schedule
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class SimpleController : Controller() {

    private lateinit var network: Network
    private val playerState = PlayerState(60)

    private var point = Point(
        Random.nextDouble(0.0, 1000.0),
        Random.nextDouble(0.0, 800.0)
    );

    init {
        var angle = 0.0
        Timer().schedule(0L, 15L) {
            angle = if (angle > 360) .0 else angle + 0.10
            point = Point(100 * cos(angle) + 300, 100 * sin(angle) + 300)
        }

        var sn: Long = 0
        Timer().schedule(0L, 50L) {
            val input = PlayerInput(sn++, Instant.now(), null)
            playerState.add(input.sn)
            network.send(input)
        }
    }

    fun connect() {
        network = Network(this)
        network.connect()
    }

    fun receive(snapshot: Long) {
        playerState.confirm(snapshot)
    }

    fun getState() : PlayerState {
        return playerState
    }


    fun getPoint(): Point {
        return point
    }

}