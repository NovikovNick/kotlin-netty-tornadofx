package com.metalheart.client.view

import com.metalheart.client.controller.ClientController
import javafx.beans.property.SimpleStringProperty
import javafx.scene.layout.HBox
import tornadofx.*

class MenuView : View() {
    override val root = HBox()

    private val msg = SimpleStringProperty()
    private val controller: ClientController by inject()

    init {

        controller.connect()

        with(root) {
            borderpane {
                left = vbox {
                    textfield(msg)
                    button("Start").setOnAction {
                        replaceWith<GameView>()
                    }
                }

            }
        }
    }
}
