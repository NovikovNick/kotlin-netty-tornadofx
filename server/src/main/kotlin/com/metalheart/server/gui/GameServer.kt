package com.metalheart.server.gui

import javafx.stage.Stage
import tornadofx.App

class GameServer : App() {
    override val primaryView = GameServerView::class


    override fun start(stage: Stage) {
        super.start(stage)
        stage.apply {
            width = 300.0
            height = 80.0
            centerOnScreen()
        }
    }
}
