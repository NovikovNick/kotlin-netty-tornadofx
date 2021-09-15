package com.metalheart.client

import com.metalheart.client.controller.ClientController
import com.metalheart.model.dto.InputDTO
import com.metalheart.model.dto.ClientInputConfirmation
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.DatagramPacket
import java.nio.charset.StandardCharsets.UTF_8

class ClientHandler(val controller: ClientController) : SimpleChannelInboundHandler<DatagramPacket>() {
    override fun channelRead0(ctx: ChannelHandlerContext?, msg: DatagramPacket?) {

        msg?.content()?.let {
            val str = it.toString(UTF_8)
            println("received from server: $str")
            when {
                str.contains(InputDTO.javaClass.typeName) -> {
                    controller.receive(InputDTO.fromString(str))
                }
                str.contains(ClientInputConfirmation.javaClass.typeName) -> {
                    controller.receive(ClientInputConfirmation.fromString(str))
                }
            }
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }
}