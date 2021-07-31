package com.metalheart.server

import com.metalheart.model.dto.ClientInput
import com.metalheart.model.dto.ClientInputConfirmation
import com.metalheart.server.gui.ServerController
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.DatagramPacket
import java.nio.charset.StandardCharsets.UTF_8

class ServerHandler(val controller: ServerController) : SimpleChannelInboundHandler<DatagramPacket>() {


    override fun channelRead0(ctx: ChannelHandlerContext?, msg: DatagramPacket?) {
        msg?.sender()?.let { client ->
            msg?.content()?.let {
                val str = it.toString(UTF_8)
                println("received: $str")
                when {
                    str.contains(ClientInput.javaClass.typeName) -> {
                        controller.receive(client, ClientInput.fromString(str))
                    }
                    str.contains(ClientInputConfirmation.javaClass.typeName) -> {
                        controller.receive(client, ClientInputConfirmation.fromString(str))
                    }
                }
            }
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }
}
