package com.metalheart.model

import org.junit.jupiter.api.Test

class PlayerStateTest {

    @Test
    fun testServerReceivingFromOneClient() {
        // arrange
        val clientId: Long = 1

        val c1 = PlayerInputBuffer(5)
        c1.add(PlayerInput(1))
        val serverState = ServerGameState()
        serverState.add(clientId, c1.getNotDeliveredInputs())

        // act
        val projection = serverState.getProjection(clientId)

        // assert
        assert(projection.confirmed.size == 1)
        assert(projection.confirmed.contains(1))
        assert(projection.updated.isEmpty())
    }

    @Test
    fun testServerReceivingFromSeveralClients() {
        // arrange
        val serverState = ServerGameState()

        val clientId1: Long = 1
        val clientId2: Long = 2
        val clientId3: Long = 3

        PlayerInputBuffer(5).apply {
            add(PlayerInput(1))
            serverState.add(clientId1, this.getNotDeliveredInputs())
        }
        PlayerInputBuffer(5).apply {
            add(PlayerInput(2))
            serverState.add(clientId2, this.getNotDeliveredInputs())
        }
        PlayerInputBuffer(5).apply {
            add(PlayerInput(3))
            serverState.add(clientId3, this.getNotDeliveredInputs())
        }

        // act
        val c1projection = serverState.getProjection(clientId1)
        val c2projection = serverState.getProjection(clientId2)
        val c3projection = serverState.getProjection(clientId3)

        // assert
        c1projection.apply {
            assert(this.confirmed.size == 1)
            assert(this.confirmed.contains(1))
            assert(this.updated.size == 2)
            assert(this.updated.containsKey(2L))
            assert(this.updated.containsKey(3L))
        }

        c2projection.apply {
            assert(this.confirmed.size == 1)
            assert(this.confirmed.contains(2))
            assert(this.updated.size == 2)
            assert(this.updated.containsKey(1L))
            assert(this.updated.containsKey(3L))
        }

        c3projection.apply {
            assert(this.confirmed.size == 1)
            assert(this.confirmed.contains(3))
            assert(this.updated.size == 2)
            assert(this.updated.containsKey(1L))
            assert(this.updated.containsKey(2L))
        }
    }

    @Test
    fun testClientConfirm() {
        // arrange
        val serverState = ServerGameState()
        val clientId: Long = 1

        val c1 = PlayerInputBuffer(5).apply {
            add(PlayerInput(1))
            add(PlayerInput(2))
            serverState.add(clientId, this.getNotDeliveredInputs())
            add(PlayerInput(3))
        }
        val projection = serverState.getProjection(clientId)
        // act
        c1.confirm(projection.confirmed, 200)
        var notDelivered = c1.getNotDeliveredInputs()

        // assert
        assert(projection.confirmed.size == 2)
        assert(projection.confirmed.contains(1))
        assert(projection.confirmed.contains(2))
        assert(notDelivered.size == 1)
        assert(notDelivered.first()?.frame == 3L)
        assert(projection.updated.isEmpty())
    }
}