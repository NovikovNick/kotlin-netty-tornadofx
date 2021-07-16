package com.metalheart.client.ext

import com.metalheart.model.Point
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color


fun Canvas.clear(): Unit {
    val gc = graphicsContext2D
    gc.fill = Color.BLACK
    gc.fillRect(.0, .0, width, height)
}


fun Canvas.draw(p: Point): Unit {
    val gc = graphicsContext2D
    gc.fill = Color.BLUE
    gc.fillOval(p.d0, p.d1, 5.0, 5.0)
}