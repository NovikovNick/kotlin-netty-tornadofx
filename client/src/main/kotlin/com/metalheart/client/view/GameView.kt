package com.metalheart.client.view

import com.metalheart.client.controller.ClientController
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
    private val controller: ClientController by inject()

    private lateinit var renderTask: TimerTask
    private val canvas = canvas(800.0, 600.0)

    init {
        with(root) {
            borderpane {
                left = vbox {
                    label { text = "FPS" }
                    slider {
                        useMaxWidth = true
                        min = 0.0
                        max = 60.0
                    }
                    hbox {
                        button("❚❚") {
                            action {
                                renderTask.cancel()
                            }
                        }
                        button("▶️") {
                            action {
                                startRendering(60)
                            }
                        }
                        button("↦") {
                            action {
                                renderFrame()
                            }
                        }
                    }

                    label { text = "Tick rate" }
                    slider {
                        useMaxWidth = true
                        min = 0.0
                        max = 60.0
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

                    button("back") {
                        useMaxWidth = true
                        action {
                            replaceWith<MenuView>()
                        }
                    }
                }
                center = canvas
            }
        }
    }


    fun startRendering(fps: Int) {
        renderTask = Timer().schedule(0L, 1000L / fps) {
            renderFrame()
        }
    }

    private fun renderFrame() {
        canvas.clear()
        canvas.graphicsContext2D.apply {

            font = Font.font(10.0)

            var i = 0
            var ping = -1
            controller.getState().forEach {
                i++

                fill = Color.WHITE
                var text = "${it.sn}"

                if (it.ack()) {
                    fill = Color.GREEN
                    Duration.between(it.sentAt, it.ackAt).toMillis().toInt()
                            .takeIf { it > ping }
                            ?.let { ping = it }

                    text = "${it.sn}"
                }
                val size = 9.0
                fillText(text, 10.0, i * size + 48.0)
                fillRect(0.0, i * size + 40.0, size, size)
            }
            canvas.drawPing(ping / 2)
        }
    }
}
