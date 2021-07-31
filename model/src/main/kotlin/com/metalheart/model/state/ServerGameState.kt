package com.metalheart.model.state

import com.metalheart.model.ClientInputProjection
import com.metalheart.model.PlayerInputBuffer
import com.metalheart.model.dto.ClientInput
import com.metalheart.model.dto.ClientInputConfirmation
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class ServerGameState {
    private var clientIdToClientFrameTimeStamp = HashMap<Long, Long>().toMutableMap()
    private var clientIdToInput = HashMap<Long, PlayerInputBuffer>().toMutableMap()
    private var clientIdToProjection = HashMap<Long, ClientInputProjection>().toMutableMap()
    private var lock = ReentrantLock()

    fun get(clientId: Long, frameTimeStamp: Long): Pair<ClientInputConfirmation, Set<ClientInput>> {
        lock.withLock {
            val confirmed = clientIdToInput[clientId]
                    ?.getNotDeliveredInputs()
                    ?.map { it.frame }
                    ?.toSet()
                    ?: emptySet()

            initProjection(clientId)
            val inputs: Set<ClientInput> = clientIdToProjection[clientId]
                    ?.inputs
                    ?.mapValues { it.value.getNotDeliveredInputs().toSet() }
                    ?.map { ClientInput(it.key, clientIdToClientFrameTimeStamp[clientId] ?: 0, it.value) }
                    ?.toSet()
                    ?: emptySet()

            return ClientInputConfirmation(clientId, confirmed) to inputs
        }
    }

    fun update(input: ClientInput) {
        lock.withLock {

            val clientId = input.clientId

            if ((clientIdToClientFrameTimeStamp[clientId] ?: 0) > input.sn) {
                clientIdToClientFrameTimeStamp.put(clientId, input.sn)
            }

            clientIdToInput.putIfAbsent(clientId, PlayerInputBuffer(60))

            initProjection(clientId)

            input.input.forEach { input ->
                clientIdToInput[clientId]?.add(input)
                clientIdToProjection.forEach { id, projection ->

                    if (id  != clientId) {
                        if (!projection.inputs.containsKey(clientId)) {
                            projection.inputs += clientId to PlayerInputBuffer(5)
                        }
                        projection.inputs[clientId]?.apply { add(input) }
                    }
                }
            }
        }
    }

    fun update(clientId: Long, frameTimeStamp: Long, confirmation: ClientInputConfirmation) {
        lock.withLock {
            clientIdToProjection[clientId]
                    ?.inputs
                    ?.get(confirmation.clientId)
                    ?.confirm(confirmation.confirmed, frameTimeStamp)
        }
    }

    private fun initProjection(clientId: Long) {
        clientIdToProjection.putIfAbsent(clientId, ClientInputProjection(HashMap(clientIdToInput.filterKeys { it != clientId })))
    }
}
