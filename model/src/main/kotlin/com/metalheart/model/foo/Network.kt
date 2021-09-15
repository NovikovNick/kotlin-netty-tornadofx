package com.metalheart.model.foo

import com.metalheart.model.PlayerInput
import com.metalheart.model.dto.ConfirmationDTO
import com.metalheart.model.dto.InputDTO
import java.time.Instant
import java.util.*

class ServerState {

    private val clientInputs: HashMap<Long, PlayerInputRingBuffer> = HashMap()
    private val clientLags: HashMap<Long, TreeMap<Long, Lag>> = HashMap()

    private val clientLastFrame: HashMap<Long, Long> = HashMap()
    private val clientProjections: HashMap<Long, HashMap<Long, PlayerInputRingBuffer>> = HashMap()

    fun startFrame(frame: Long): ServerNetcodeData {

        clientLags.forEach { (_, lags) -> lags[frame] = Lag(frame) }

        // inputs. snd client doesn't receive input in fst iteration
        val inputs: HashSet<InputDTO> = HashSet()
        clientProjections.forEach { (clientId, projection) ->
            projection.forEach { (otherClientId, inputBuffer) ->
                inputBuffer.getUnconfirmed()
                        .map { InputDTO(clientId, it.frame, clientLastFrame[clientId]!!, it) }
                        .forEach { inputs.add(it) }
            }
        }

        // ack
        val acknowledgements: Set<ServerAck> = clientInputs.flatMap { (clientId, inputBuffer) ->
            inputBuffer.getUnconfirmed().map { ServerAck(clientId, frame, it.frame) }
        }.toSet()

        // conf
        val confirmations: Set<ConfirmationDTO> = emptySet()

        return ServerNetcodeData(inputs, acknowledgements, confirmations)
    }

    fun update(dto: InputDTO) {
        val (clientId, clientFrame, serverFrame, input) = dto
        println("\t\tserver input[$clientId]: $dto")

        clientLastFrame.compute(clientId) { _, v -> if (v == null || v < clientFrame) clientFrame else v }

        clientInputs.putIfAbsent(clientId, PlayerInputRingBuffer(10))

        for (otherClientId in clientInputs.keys) {
            clientProjections.putIfAbsent(otherClientId, HashMap())
            val projection = clientProjections[otherClientId]!!

            for (projClientId in clientInputs.keys) {
                if (otherClientId != projClientId) {
                    projection.putIfAbsent(projClientId, PlayerInputRingBuffer(5))
                }
            }
        }

        clientLags.putIfAbsent(clientId, TreeMap())
        clientLags[clientId]!!.let { lags ->


            val syncFrame = lags[serverFrame]
                    ?.let { it.lag?.let { lag -> serverFrame - lag } }
                    ?: 0


            clientInputs[clientId]?.let { inputBuffer ->
                inputBuffer.add(syncFrame, input)
            }

            clientProjections.forEach { (id, projection) ->
                if (id != clientId) {
                    projection.forEach { (id, inputBuffer) ->
                        inputBuffer.add(syncFrame, input)
                    }
                }
            }
        }
    }

    fun update(ack: ClientAck, timestamp: Long) {
        val (fromClientId, ackClientId, clientFrame, srvFrame) = ack
        println("\t\tserver ack: $ack")

        clientLags.putIfAbsent(fromClientId, TreeMap())
        clientLags[fromClientId]!!.let { lags ->
            lags[srvFrame]?.let {
                lag -> lag.calculate(timestamp)
            }
        }

        val projection = getProjection(fromClientId, ackClientId)
        projection[ackClientId]!!.ack(clientFrame)
    }

    fun update(confirm: Confirmation) {
        val (fromClientId, ackClientId, frame) = confirm // todo: check fromClientId
        println("\t\tserver conf: $confirm")

        clientInputs[ackClientId]?.let { inputBuffer ->
            inputBuffer.confirm(frame)
        }
    }


    private fun getProjection(fromClientId: Long, ackClientId: Long): java.util.HashMap<Long, PlayerInputRingBuffer> {
        clientProjections.putIfAbsent(fromClientId, HashMap())
        val projection = clientProjections[fromClientId]!!
        projection.putIfAbsent(ackClientId, PlayerInputRingBuffer(10))
        return projection
    }

    private fun getTimestamp() = Instant.now().toEpochMilli()
}

class Lag(private val sentAt: Long) {

    var lag: Long? = null

    fun calculate(receiveAt: Long) {
        lag = (receiveAt - sentAt) / 2
    }
}

class PlayerInputRingBuffer(private val capacity: Short) {

    private var inputs: HashSet<PlayerInputWrapper> = HashSet()

    fun add(syncFrame: Long, input: PlayerInput) {
        inputs.add(PlayerInputWrapper(syncFrame, input))
    }

    fun ack(frame: Long) {
        inputs.forEach {
            if (it.input.frame == frame) {
                it.ack = true
            }
        }
    }

    fun confirm(frame: Long) {
        inputs.forEach {
            if (it.input.frame == frame) {
                it.confirmed = true
            }
        }
    }


    fun getUnconfirmed(): Set<PlayerInput> {
        val res = inputs.filter { !it.confirmed }.map { it.input }.toSet()
        return res
    }

    fun getConfirmed(): Set<PlayerInput> {
        return inputs.filter { it.confirmed }.map { it.input }.toSet()
    }

    // todo add sync flag
    data class PlayerInputWrapper(val syncFrame: Long,
                                  val input: PlayerInput,
                                  var ack: Boolean = false,
                                  var confirmed: Boolean = false)
}

data class ServerAck(val clientId: Long,
                     val srcFrame: Long,
                     val dstFrame: Long)

data class ClientAck(val fromClientId: Long,
                     val ackClientId: Long,
                     val srcFrame: Long,
                     val dstFrame: Long)


data class Confirmation(val fromClientId: Long,
                        val ackClientId: Long,
                        val dstFrame: Long)

class State {

}

class Scene {

}