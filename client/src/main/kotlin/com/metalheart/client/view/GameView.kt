package com.metalheart.client.view

import com.metalheart.client.controller.SimpleController
import com.metalheart.client.ext.clear
import com.metalheart.client.ext.draw
import javafx.scene.layout.HBox
import tornadofx.*
import java.util.*
import kotlin.concurrent.schedule

class GameView : View() {
    override val root = HBox()
    private val controller: SimpleController by inject()

    init {


        val canvas = canvas(800.0, 600.0)

        Timer().schedule(0L, 15L) {
            val p = controller.getPoint()
            canvas.clear()
            canvas.draw(p)
        }


        var ping = 0

        with(root) {
            borderpane {
                left = vbox {
                    label("ping: ${ping}")
                    button("clear") {
                        useMaxWidth = true
                        action {
                            canvas.clear()
                        }
                    }
                    button("random point") {
                        useMaxWidth = true
                        action {

                            canvas.draw(controller.getPoint())
                        }
                    }
                    button("replace point") {
                        useMaxWidth = true
                        action {
                            canvas.clear()
                            canvas.draw(controller.getPoint())
                        }
                    }
                    button("Back") {
                        useMaxWidth = true
                        action {
                            replaceWith<MainMenuView>()
                        }
                    }
                }
                center = canvas
            }
        }
    }
}

