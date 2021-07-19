package com.metalheart.client.view

import com.metalheart.client.controller.SimpleController
import javafx.beans.property.SimpleStringProperty
import javafx.scene.layout.HBox
import tornadofx.*

class MainMenuView : View() {
    override val root = HBox()

    private val msg = SimpleStringProperty()
    private val controller: SimpleController by inject()

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