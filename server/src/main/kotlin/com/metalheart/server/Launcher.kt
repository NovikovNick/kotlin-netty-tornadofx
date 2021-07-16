package com.metalheart.server

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioDatagramChannel


fun main(args: Array<String>) {
    val workerGroup: EventLoopGroup = NioEventLoopGroup()
    try {
        val b = Bootstrap()
        b.group(workerGroup)
                .channel(NioDatagramChannel::class.java)
                .handler(object : ChannelInitializer<NioDatagramChannel>() {
                    @Throws(Exception::class)
                    override fun initChannel(ch: NioDatagramChannel) {
                        ch.pipeline().addLast(EchoHandler())
                    }
                })
                .option(ChannelOption.SO_BROADCAST, true)

        val f: ChannelFuture = b.bind(8080).sync()
        f.channel().closeFuture().sync()

    } finally {
        workerGroup.shutdownGracefully()
    }
}
