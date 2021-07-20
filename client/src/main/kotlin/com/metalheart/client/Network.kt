package com.metalheart.client

import com.metalheart.client.controller.ClientController
import com.metalheart.model.PlayerInput
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.DatagramPacket
import io.netty.channel.socket.nio.NioDatagramChannel
import io.netty.handler.codec.string.StringDecoder
import io.netty.handler.codec.string.StringEncoder
import java.nio.charset.StandardCharsets
import kotlin.concurrent.thread

class Network (val controller: ClientController) {

    private lateinit var channel: NioDatagramChannel

    fun connect() {
        thread {
            val workerGroup: EventLoopGroup = NioEventLoopGroup(1)

            val b = Bootstrap()
            b.group(workerGroup)
            b.channel(NioDatagramChannel::class.java)
            b.handler(object : ChannelInitializer<NioDatagramChannel>() {
                @Throws(Exception::class)
                override fun initChannel(ch: NioDatagramChannel) {
                    channel = ch
                    channel.pipeline().addLast(StringDecoder(), StringEncoder(), ClientHandler(controller))
                }
            })

            var future = b.connect("localhost", 8080).sync();
            future.channel().closeFuture().sync()
        }
    }

    fun send(input: PlayerInput) {
        if (this::channel.isInitialized) {
            val buf = Unpooled.wrappedBuffer(input.sn.toString().toByteArray(StandardCharsets.UTF_8));
            val packet = DatagramPacket(buf, channel.remoteAddress())
            channel.writeAndFlush(packet)
        }
    }
}