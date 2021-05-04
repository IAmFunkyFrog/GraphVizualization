package graphVizualization.view

import Vertex
import graphVizualization.controller.VertexController
import javafx.beans.property.BooleanProperty
import javafx.geometry.Point2D
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import kotlin.random.Random
import kotlin.random.nextUInt
import tornadofx.*

class VertexView(
    val vertex: Vertex
): Circle() {

    private val controller = VertexController()

    var visibleText: BooleanProperty = booleanProperty(false)

    init {
        radius = 15.0
        centerX = Random.nextUInt().toDouble() % 640
        centerY = Random.nextUInt().toDouble() % 640
        fill = Color.RED

        this.setOnMouseDragged {
            controller.onDrag(it, this)
        }
        this.setOnMouseEntered {
            controller.onMouseEntered(this)
        }
        this.setOnMouseExited {
            controller.onMouseExited(this)
        }
    }

    constructor(vertex: Vertex, point: Point2D): this(vertex) {
        centerX = point.x
        centerY = point.y
    }

    val label = text {
        text = vertex.value
        fill = Color.BLUE
        xProperty().bind(centerXProperty() + radius)
        yProperty().bind(centerYProperty() - radius)
        visibleWhen(visibleText)
    }

    fun applyDisplacement(displacement: Point2D) {
        centerX += displacement.x
        centerY += displacement.y
    }

}