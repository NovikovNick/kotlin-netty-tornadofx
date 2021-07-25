package com.metalheart.model

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class ServerGameState {
    private var allInputs: Map<Long, PlayerInputBuffer> = HashMap()
    private var clientToInputs: Map<Long, ClientInputProjection> = HashMap()
    private var lock = ReentrantLock()

    fun add(clientId: Long, inputs: List<PlayerInput>) {
        lock.withLock {

            if (!clientToInputs.containsKey(clientId)) {
                val projection = ClientInputProjection(HashMap(allInputs))
                allInputs += clientId.to(PlayerInputBuffer(5))
                clientToInputs += clientId.to(projection)
            }

            clientToInputs.forEach { otherClientId, buffer ->
                inputs.forEach { input ->
                    allInputs[clientId]?.add(input)
                    if (!buffer.inputs.containsKey(clientId)) {
                        buffer.inputs += clientId.to(PlayerInputBuffer(5))
                    }
                    buffer.inputs[clientId]?.let { it.add(input) }
                }
            }
        }
    }

    fun getProjection(clientId: Long): InputUpdatedClientProjection {
        lock.withLock {
            var confirmed: Set<Long> = HashSet()
            var updated: Map<Long, Set<PlayerInput>> = HashMap()
            clientToInputs[clientId]?.let {
                it.inputs.forEach { otherClientId, buffer ->

                    if (otherClientId == clientId) {
                        buffer.getNotDeliveredInputs().forEach { confirmed += it.frame }
                    } else {
                        val notDelivered = buffer.getNotDeliveredInputs().toSet()
                        updated += otherClientId.to(notDelivered)
                    }
                }
            }
            return InputUpdatedClientProjection(confirmed, updated)
        }
    }
}
