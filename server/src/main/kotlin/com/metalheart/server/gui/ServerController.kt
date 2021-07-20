package com.metalheart.server.gui

import com.metalheart.server.EchoHandler
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.DatagramPacket
import io.netty.channel.socket.nio.NioDatagramChannel
import tornadofx.Controller
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.schedule
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

class ServerController : Controller() {

    private var inputs: Map<InetSocketAddress, ArrayList<Long>> = HashMap()
    private var lock = ReentrantLock()
    private lateinit var channel: Channel
    private lateinit var syncTask: TimerTask

    fun bind(port: Int) {
        thread {
            val workerGroup: EventLoopGroup = NioEventLoopGroup()
            try {
                val b = Bootstrap()
                b.group(workerGroup)
                        .channel(NioDatagramChannel::class.java)
                        .handler(object : ChannelInitializer<NioDatagramChannel>() {
                            @Throws(Exception::class)
                            override fun initChannel(ch: NioDatagramChannel) {
                                ch.pipeline().addLast(EchoHandler(this@ServerController))
                            }
                        })
                        .option(ChannelOption.SO_BROADCAST, true)

                val f: ChannelFuture = b.bind(port).sync()
                channel = f.channel()
                println("Server bind $port")
                channel.closeFuture().sync()

            } finally {
                workerGroup.shutdownGracefully()
            }
        }
    }

    fun saveInput(sender: InetSocketAddress, input: Long) {
        lock.withLock {
            if (!inputs.containsKey(sender)) {
                inputs += sender.to(ArrayList())
            }
            inputs[sender]?.add(input)
        }
    }

    fun stopSync() {
        syncTask.cancel()
    }

    fun startSync(tickRate: Int) {
        syncTask = Timer().schedule(0L, 1000L / tickRate) { sync() }
    }

    fun sync() {
        if (this::channel.isInitialized) {
            inputs.forEach { client, input ->
                thread {

                    // TimeUnit.MILLISECONDS.sleep(Random().nextInt(500) + 100L)
                    input.forEach {

                        println("sent to $client: $it")
                        val buf = Unpooled.wrappedBuffer(it.toString().toByteArray())
                        channel.writeAndFlush(DatagramPacket(buf, client))
                    }
                    inputs += client.to(ArrayList())
                }
            }
        }
    }

}
