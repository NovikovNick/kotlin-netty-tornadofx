package com.metalheart.server.gui

import javafx.scene.layout.HBox
import tornadofx.View
import tornadofx.action
import tornadofx.button
import tornadofx.hbox

class GameServerView : View() {
    override val root = HBox()

    private val controller: ServerController by inject()

    init {
        with(root) {

            button("connect") {
                action {
                    controller.bind(8080)
                }
            }
            hbox {
                button("❚❚") {
                    action {
                        controller.stopSync()
                    }
                }
                button("▶️") {
                    action {
                        controller.startSync(20)
                    }
                }
                button("↦") {
                    action {
                        controller.sync()
                    }
                }
            }
        }
    }
}
