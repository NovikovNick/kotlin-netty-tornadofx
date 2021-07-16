/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package com.metalheart.client

import com.metalheart.client.view.MainMenuView
import javafx.application.Application
import javafx.stage.Stage
import tornadofx.App
import tornadofx.importStylesheet

class GameClient : App() {
    override val primaryView = MainMenuView::class


    override fun start(stage: Stage) {
        super.start(stage)
        stage.apply {
            width = 1800.0
            height = 900.0
            centerOnScreen()
        }
    }

    init {
        importStylesheet(Styles::class)
    }

}

fun main(args: Array<String>) {
    Application.launch(GameClient::class.java, *args)
}