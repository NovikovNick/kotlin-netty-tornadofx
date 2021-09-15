package com.metalheart.model.state

class ClientGameState(val clientId: Long) /*: ClientInputView*/{
    /*private var serverFrameTimeStamp: Long = 0L
    private val selfInputs = PlayerInputBuffer(60)
    private var otherPlayerInputs = HashMap<Long, PlayerInputBuffer>().toMutableMap()
    private var lock = ReentrantLock()

    fun get(): Pair<Input, Set<ClientInputConfirmation>> {
        lock.withLock {

            // todo: how to use ClientInputConfirmation???

            val input = selfInputs.getNotDeliveredInputs()

            val otherClientConfirmation: Set<ClientInputConfirmation> = otherPlayerInputs
                    .mapValues { it.value.getNotDeliveredInputs().map { it.frame }.toSet() }
                    .map { ClientInputConfirmation(it.key, it.value) }
                    .toSet()

            return Input(clientId, serverFrameTimeStamp, input.toSet()) to otherClientConfirmation
        }
    }

    fun add(input: PlayerInput) {
        lock.withLock {
            selfInputs.add(input)
        }
    }

    fun update(input: Input) {
        lock.withLock {

            serverFrameTimeStamp = if (serverFrameTimeStamp > input.sn) serverFrameTimeStamp else input.sn

            otherPlayerInputs.putIfAbsent(input.clientId, PlayerInputBuffer(60))

            input.input.forEach { otherPlayerInputs[input.clientId]?.add(it) }
        }
    }

    fun update(confirmation: ClientInputConfirmation, frameTimeStamp: Long) {
        lock.withLock {
            selfInputs.confirm(confirmation.confirmed, frameTimeStamp)
        }
    }

    override fun getInputs(): Map<Long, List<PlayerInput>> {

        val res = HashMap(otherPlayerInputs.mapValues { it.value.getAllInputs() })
        res += (clientId to selfInputs.getAllInputs())
        return res
    }*/
}
