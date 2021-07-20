package com.metalheart.server

import com.metalheart.server.gui.GameServer
import javafx.application.Application


fun main(args: Array<String>) {
    Application.launch(GameServer::class.java, *args)
}
