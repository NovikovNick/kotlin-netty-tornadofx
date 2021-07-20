package com.metalheart.server

import com.metalheart.server.gui.ServerController
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.DatagramPacket
import java.nio.charset.StandardCharsets.UTF_8

class EchoHandler(val controller: ServerController) : SimpleChannelInboundHandler<DatagramPacket>() {


    override fun channelRead0(ctx: ChannelHandlerContext?, msg: DatagramPacket?) {
        msg?.sender()?.let { client ->
            msg?.content()?.let {
                println("received from $client: ${it.toString(UTF_8).toLong()}")
                controller.saveInput(client, it.toString(UTF_8).toLong())
            }
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }
}
