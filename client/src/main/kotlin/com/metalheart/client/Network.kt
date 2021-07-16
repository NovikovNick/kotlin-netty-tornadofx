package com.metalheart.client

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.string.StringDecoder
import io.netty.handler.codec.string.StringEncoder
import tornadofx.runAsync

class Network {

    private lateinit var channel : SocketChannel

    fun connect() {

        runAsync {
            val workerGroup: EventLoopGroup = NioEventLoopGroup()

            val b = Bootstrap()
            b.group(workerGroup)

            b.channel(NioSocketChannel::class.java)

            b.handler(object : ChannelInitializer<SocketChannel>() {
                @Throws(Exception::class)
                override fun initChannel(ch: SocketChannel) {
                    channel = ch
                    channel.pipeline().addLast(StringDecoder(), StringEncoder())
                }
            })

            var future = b.connect("localhost", 8080).sync();
            future.channel().closeFuture().sync()

        }
    }

    fun send(text: String) {
        if (this::channel.isInitialized) {
            channel.writeAndFlush(text)
        }
    }
}