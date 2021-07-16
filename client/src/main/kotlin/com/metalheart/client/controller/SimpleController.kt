package com.metalheart.client.controller

import com.metalheart.client.Network
import com.metalheart.model.Point
import tornadofx.Controller
import java.util.*
import kotlin.concurrent.schedule
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class SimpleController : Controller() {

    lateinit var network: Network
    private var point = Point(
        Random.nextDouble(0.0, 1000.0),
        Random.nextDouble(0.0, 800.0)
    );

    init {
        var angle = 0.0
        Timer().schedule(0L, 15L) {
            angle = if (angle > 360) .0 else angle + 0.05
            point = Point(100 * cos(angle) + 300, 100 * sin(angle) + 300)
        }
    }

    fun connect() {
        network = Network()
        network.connect()
    }

    fun send(text: String) {
        network.send(text)
    }

    fun getPoint(): Point {
        return point
    }

}