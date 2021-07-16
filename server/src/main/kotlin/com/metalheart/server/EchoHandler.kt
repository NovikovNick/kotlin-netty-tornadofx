package com.metalheart.server

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import java.time.ZonedDateTime

class EchoHandler: SimpleChannelInboundHandler<String>() {
    override fun channelRead0(ctx: ChannelHandlerContext?, msg: String?) {
        println("${ZonedDateTime.now()}: $msg")
    }
}
