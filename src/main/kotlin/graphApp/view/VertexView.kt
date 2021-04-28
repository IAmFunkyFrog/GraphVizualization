package graphApp.view

import Vertex
import javafx.geometry.Point2D
import javafx.scene.shape.Circle
import java.awt.Color
import kotlin.random.Random
import kotlin.random.nextUInt
class VertexView<V>(
    val vertex: Vertex<V>
): Circle() {

    init {
        radius = 5.0
        centerX = Random.nextUInt().toDouble() % 1000
        centerY = Random.nextUInt().toDouble() % 1000
    }

    constructor(vertex: Vertex<V>, point: Point2D): this(vertex) {
        centerX = point.x
        centerY = point.y
    }

    fun applyDisplacement(displacement: Point2D) {
        centerX += displacement.x
        centerY += displacement.y
    }

}