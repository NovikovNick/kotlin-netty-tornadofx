package com.metalheart.client

import com.metalheart.client.controller.ClientController
import com.metalheart.model.InputUpdatedClientProjection
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.DatagramPacket
import java.nio.charset.StandardCharsets.UTF_8

class ClientHandler(val controller: ClientController) : SimpleChannelInboundHandler<DatagramPacket>() {
    override fun channelRead0(ctx: ChannelHandlerContext?, msg: DatagramPacket?) {

        msg?.content()?.let {
            val snapshot = InputUpdatedClientProjection.fromString(it.toString(UTF_8))
            println("received from server: $snapshot")
            controller.receive(snapshot.confirmed)
        }
    }
}