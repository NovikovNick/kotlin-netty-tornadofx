package com.metalheart.model.foo

import com.metalheart.model.PlayerInput
import com.metalheart.model.dto.ConfirmationDTO
import com.metalheart.model.dto.InputDTO
import java.util.*
import kotlin.collections.HashSet

class ClientState(val clientId: Long) {

    private val serverLag: TreeMap<Long, Long> = TreeMap()
    private var serverLastFrame: Long = 0

    private val selfInputs = PlayerInputRingBuffer(10)
    private val acknowledgements: HashSet<ServerAck> = HashSet()

    private val otherInputs: TreeMap<Long, PlayerInputRingBuffer> = TreeMap()
    private val confirmed: HashSet<Long> = HashSet()


    fun startFrame(input: PlayerInput): ClientNetcodeData {
        val frame = input.frame
        serverLag[frame] = frame

        selfInputs.add(frame, input)

        // inputs
        val inputs: Set<InputDTO> = selfInputs.getUnconfirmed().map { InputDTO(clientId, it.frame, serverLastFrame, it) }.toSet()

        // ack. add delayed check if delivered
        val acknowledgements: Set<ServerAck> =  acknowledgements.toSet()
        confirmed.clear()

        // conf. add delayed check if delivered
        val confirmations: Set<ConfirmationDTO> = confirmed.map { ConfirmationDTO(clientId, it) }.toSet()
        confirmed.clear()

        return ClientNetcodeData(inputs, acknowledgements, confirmations)
    }

    fun update(dto: InputDTO) {
        val (otherClientId, srvFrame, clientFrame, input) = dto
        println("\t\tclient[$clientId] input: $dto")

        otherInputs.putIfAbsent(otherClientId, PlayerInputRingBuffer(10))
        otherInputs[otherClientId]?.let { inputBuffer ->
            val syncFrame = input.frame
            inputBuffer.add(syncFrame, input)

            acknowledgements.add(ServerAck(otherClientId, clientFrame, srvFrame))
        }
    }

    fun update(ack: ServerAck, timestamp: Long) {

        val (clientId, serverFrame, clientFrame) = ack
        println("\t\tclient[$clientId] ack: $ack")


        serverLastFrame = if (serverLastFrame > serverFrame) serverLastFrame else serverFrame

        selfInputs.confirm(clientFrame)
        serverLag[clientFrame] = timestamp - serverLag.getOrDefault(clientFrame, 0)

        confirmed.add(clientFrame)

    }

    fun update(confirm: ConfirmationDTO) {
        val (clientId, frame) = confirm
        println("\t\tclient[$clientId] confirm: $confirm")
    }
}