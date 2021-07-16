package com.metalheart.server

import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.DatagramPacket
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets.UTF_8

class EchoHandler : SimpleChannelInboundHandler<DatagramPacket>() {

    var clients: Set<InetSocketAddress> = HashSet()

    override fun channelRead0(ctx: ChannelHandlerContext?, msg: DatagramPacket?) {
        msg?.sender()?.let {
            clients += it
            val input = "Received from $it: ${msg?.content()?.toString(UTF_8)}"
            println(input)
            val buf = Unpooled.wrappedBuffer("done".toByteArray(UTF_8));
            ctx?.channel()?.writeAndFlush(DatagramPacket(buf, it))

            clients.forEach {
                val buf = Unpooled.wrappedBuffer(input.toByteArray(UTF_8));
                ctx?.channel()?.writeAndFlush(DatagramPacket(buf, it))
            }
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }
}
