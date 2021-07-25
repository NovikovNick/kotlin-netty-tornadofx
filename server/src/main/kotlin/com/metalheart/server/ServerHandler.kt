package com.metalheart.server

import com.metalheart.model.ClientInputData
import com.metalheart.server.gui.ServerController
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.DatagramPacket
import java.nio.charset.StandardCharsets.UTF_8

class ServerHandler(val controller: ServerController) : SimpleChannelInboundHandler<DatagramPacket>() {


    override fun channelRead0(ctx: ChannelHandlerContext?, msg: DatagramPacket?) {
        msg?.sender()?.let { client ->
            msg?.content()?.let {
                println("received from $client: ${it.toString(UTF_8)}")
                val snapshot = ClientInputData.fromString(it.toString(UTF_8))
                controller.saveInput(client, snapshot)
            }
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }
}
