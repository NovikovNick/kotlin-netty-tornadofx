package com.metalheart.model

import com.metalheart.model.state.ClientGameState
import com.metalheart.model.state.ServerGameState
import org.junit.jupiter.api.Test

class ClientPartTest {

    @Test
    fun clientReceiveConfirmationTest() {

        // arrange
        val server = ServerGameState()
        val c1 = ClientGameState(1).apply {
            add(PlayerInput(1))
            val (input, confirmations) = get()
            server.update(input)
            confirmations.forEach { server.update(clientId, 100, it) }
        }

        // act
        val (conf, input) = server.get(c1.clientId, 200)

        // assert
        assert(conf.confirmed.size == 1) { "client doesn't receive confirmation" }
        assert(conf.confirmed.contains(1)) { "client doesn't receive confirmation" }
        assert(input.size == 0) { "client receives self's input" }
    }

    @Test
    fun clientReceiveInputsTest() {

        // arrange
        val server = ServerGameState()
        val c1 = ClientGameState(1).apply {
            add(PlayerInput(1))
            val (input, confirmations) = get()
            server.update(input)
            confirmations.forEach { server.update(clientId, 100, it) }
        }

        val c2 = ClientGameState(2).apply {
            add(PlayerInput(2))
            val (input, confirmations) = get()
            server.update(input)
            confirmations.forEach { server.update(clientId, 100, it) }
        }

        // act
        val (c1conf, c1input) = server.get(c1.clientId, 200)
        val (c2conf, c2input) = server.get(c2.clientId, 200)

        // assert
        assert(c1input.size == 1)
        assert(c1input.first().clientId == 2L)
        assert(c2input.size == 1)
        assert(c2input.first().clientId == 1L)
    }

    @Test
    fun clientDoesntSendConfirmedInputsTest() {

        // arrange
        val server = ServerGameState()

        // c1 send input to server
        val c1 = ClientGameState(2).apply {
            add(PlayerInput(1))
            val (input, confirmations) = get()
            server.update(input)
            confirmations.forEach { server.update(clientId, 100, it) }
        }

        // server sent update to c1
        server.get(c1.clientId, 200).apply {
            c1.update(first, 320)
            second.forEach { c1.update(it) }
        }

        // c1 send confirmation to server
        c1.add(PlayerInput(111))

        // act
        val (c1Input, c1Confirm) = c1.get()

        // assert
        assert(c1Input.input.size == 1)
        assert(c1Input.input.first().frame == 111L)
    }

    @Test
    fun clientDoesntReceiveConfirmedInputsTest() {

        // arrange
        val server = ServerGameState()
        val c1 = ClientGameState(1)

        // c2 send input to server
        val c2 = ClientGameState(2).apply {
            add(PlayerInput(1))
            val (input, confirmations) = get()
            server.update(input)
            confirmations.forEach { server.update(clientId, 100, it) }
        }

        // server sent update to c1
        server.get(c1.clientId, 200).apply {
            c1.update(first, 320)
            second.forEach { c1.update(it) }
        }

        // c1 send confirmation to server
        c1.apply {
            val (input, confirmations) = get()
            server.update(input)
            confirmations.forEach { server.update(clientId, 200, it) }
        }

        // c2 send second input to server
        c2.apply {
            add(PlayerInput(111))
            val (input, confirmations) = get()
            server.update(input)
            confirmations.forEach { server.update(clientId, 200, it) }
        }

        // act
        val (c1conf, c1input) = server.get(c1.clientId, 200)

        // assert
        assert(c1input.size == 1)
        assert(c1input.first().clientId == 2L)
        assert(c1input.first().input.size == 1)
        assert(c1input.first().input.first().frame == 111L)
    }
}