package graphVizualization.view

import Vertex
import graphVizualization.controller.GraphController
import graphVizualization.controller.VertexController
import javafx.beans.property.BooleanProperty
import javafx.beans.property.BooleanProperty.booleanProperty
import javafx.beans.property.BooleanPropertyBase
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Point2D
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import kotlin.random.Random
import kotlin.random.nextUInt
import tornadofx.*
import kotlin.math.absoluteValue

class VertexView(
    val vertex: Vertex
): Circle() {

    val controller = VertexController()

    var visibleText: BooleanProperty = SimpleBooleanProperty(false)

    init {
        radius = 15.0
        centerX = Random.nextInt().absoluteValue.toDouble() % 400
        centerY = Random.nextInt().absoluteValue.toDouble() % 400
        fill = Color.RED
    }

    constructor(vertex: Vertex, point: Point2D): this(vertex) {
        centerX = point.x
        centerY = point.y
    }

    val label = text {
        text = vertex.value
        fill = Color.BLUE
        xProperty().bind(centerXProperty() + radiusProperty())
        yProperty().bind(centerYProperty() - radiusProperty())
        visibleWhen(visibleText)
    }

    fun applyDisplacement(displacement: Point2D) {
        centerX += displacement.x
        centerY += displacement.y
    }

}