package com.metalheart.server.gui

import com.metalheart.model.dto.InputDTO
import com.metalheart.model.dto.ClientInputConfirmation
import com.metalheart.model.state.ServerGameState
import com.metalheart.server.ServerHandler
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.DatagramPacket
import io.netty.channel.socket.nio.NioDatagramChannel
import tornadofx.Controller
import java.net.InetSocketAddress
import java.time.Instant
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.schedule
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

class ServerController : Controller() {

    private var lock = ReentrantLock()
    private lateinit var channel: Channel
    private lateinit var syncTask: TimerTask

    private var clients: Map<InetSocketAddress, Long> = HashMap()
    private val serverState = ServerGameState()


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
                                ch.pipeline().addLast(ServerHandler(this@ServerController))
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

    fun receive(sender: InetSocketAddress, input: InputDTO) {
        lock.withLock {
            if (!clients.containsKey(sender)) {
                clients += sender.to(input.clientId)
            }

            serverState.update(input)
        }
    }

    fun receive(sender: InetSocketAddress, input: ClientInputConfirmation) {
        lock.withLock {
            if (sender in clients) {
                val clientId = clients[sender]!!
                serverState.update(clientId, Instant.now().toEpochMilli(), input)
            }
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
            clients.forEach { address, clientId ->
                thread {

                    // TimeUnit.MILLISECONDS.sleep(Random().nextInt(500) + 100L)

                    val (conf, input) = serverState.get(clientId, Instant.now().toEpochMilli())

                    channel.writeAndFlush(DatagramPacket(Unpooled.wrappedBuffer(conf.toByteArray()), address))
                    input.forEach {
                        channel.writeAndFlush(DatagramPacket(Unpooled.wrappedBuffer(it.toByteArray()), address))
                    }
                }
            }
        }
    }

}
