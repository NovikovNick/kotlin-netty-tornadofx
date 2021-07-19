package com.metalheart.client.view

import com.metalheart.client.controller.SimpleController
import com.metalheart.client.ext.clear
import com.metalheart.client.ext.draw
import com.metalheart.client.ext.drawPing
import com.metalheart.model.Point
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import tornadofx.*
import java.time.Duration
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
            canvas.draw(Point(300.0, 300.0))

            canvas.graphicsContext2D.apply {
                stroke = Color.WHITE
                strokeLine(p.d0, p.d1, 303.0, 303.0)

                font = Font.font(10.0)

                var i = 0
                var ping = -1;
                controller.getState().forEach {
                    i++

                    fill = Color.WHITE
                    var text = "${it.sn}"

                    if (it.ack()) {
                        fill = Color.GREEN
                        Duration.between(it.sentAt, it.ackAt).toMillis().toInt()
                                .takeIf { it > ping }
                                ?.let { ping = it  }

                        text =  "${it.sn}"
                    }
                    val size = 9.0
                    fillText(text, 10.0, i * size + 48.0)
                    fillRect(0.0, i * size + 40.0, size, size)
                }
                canvas.drawPing(ping)
            }

        }

        with(root) {
            borderpane {
                left = vbox {

                    button("next frame") {
                        useMaxWidth = true
                        action {

                            canvas.draw(controller.getPoint())
                        }
                    }
                    button("prev frame") {
                        useMaxWidth = true
                        action {
                            canvas.clear()
                            canvas.draw(controller.getPoint())
                        }
                    }
                    button("clear") {
                        useMaxWidth = true
                        action {
                            canvas.clear()
                        }
                    }
                    button("back") {
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

