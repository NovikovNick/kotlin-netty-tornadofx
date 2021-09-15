package com.metalheart.model

import com.metalheart.model.foo.*
import org.junit.jupiter.api.Test
import kotlin.random.Random


class NetworkTest {

    @Test
    fun test() {
        // arrange
        val server = ServerState()
        val c1 = ClientState(1)
        val c2 = ClientState(2)

        val random = Random(10)

        for (i in 1..10) {

            println("$i:")
            val frame = i * 50L
            c1.startFrame(PlayerInput(frame)).let { (inputs, ack, conf) ->

                println("Client" +
                        "\n\tinputs: $inputs" +
                        "\n\tack: $ack" +
                        "\n\tconf: $conf")

                inputs.forEach { server.update(it) }
                ack.forEach { (clientId, srcFrame, dstFrame) ->
                    server.update(ClientAck(c1.clientId, clientId, srcFrame, dstFrame), frame + 1000 + 20)
                }
                conf.forEach { (clientId, dstFrame) -> server.update(Confirmation(c1.clientId, clientId, dstFrame)) }
            }

            c2.startFrame(PlayerInput(frame + 5)).let { (inputs, ack, conf) ->

                println("Client" +
                        "\n\tinputs: $inputs" +
                        "\n\tack: $ack" +
                        "\n\tconf: $conf")

                inputs.forEach { server.update(it) }
                ack.forEach { (clientId, srcFrame, dstFrame) ->
                    server.update(ClientAck(c1.clientId, clientId, srcFrame, dstFrame), frame + 1000 + 5 + 20)
                }
                conf.forEach { (clientId, dstFrame) -> server.update(Confirmation(c1.clientId, clientId, dstFrame)) }
            }

            server.startFrame(frame + 1000).let { (inputs, ack, conf) ->

                println("Server" +
                        "\n\tinputs: $inputs" +
                        "\n\tack: $ack" +
                        "\n\tconf: $conf")

                inputs.groupBy { it.clientId }.let {
                    it[c1.clientId]?.forEach { c1.update(it) }
                    it[c2.clientId]?.forEach { c2.update(it) }
                }

                ack.groupBy { it.clientId }.let {
                    it[c1.clientId]?.forEach { c1.update(it, frame + 20) }
                    it[c2.clientId]?.forEach { c2.update(it, frame + 5 + 20) }
                }

                conf.groupBy { it.clientId }.let {
                    it[c1.clientId]?.forEach { c1.update(it) }
                    it[c2.clientId]?.forEach { c2.update(it) }
                }
            }

            println("------- Finished\n\n")
        }
        // act
        // assert
    }


    @Test
    fun bufferTest() {
        val buf = PlayerInputRingBuffer(5)
        buf.add(50, PlayerInput(50))
        buf.add(100, PlayerInput(100))
        buf.confirm(50)
        val conf = buf.getConfirmed()
        val unconf = buf.getUnconfirmed()

        assert(conf.size == 1)
        assert(unconf.size == 1)
    }
}