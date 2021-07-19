package com.metalheart.client

import com.metalheart.client.controller.SimpleController
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.DatagramPacket
import java.nio.charset.StandardCharsets.UTF_8

class ClientHandler(val controller: SimpleController) : SimpleChannelInboundHandler<DatagramPacket>() {
    override fun channelRead0(ctx: ChannelHandlerContext?, msg: DatagramPacket?) {
        println("Received: ${msg?.content()?.toString(UTF_8)}")
        msg?.content()?.let {
            controller.receive(it.toString(UTF_8).toLong())
        }
    }
}