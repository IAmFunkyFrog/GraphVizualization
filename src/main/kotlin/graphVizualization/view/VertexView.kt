package graphVizualization.view

import Vertex
import graphVizualization.controller.VertexController
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Point2D
import javafx.scene.Parent
import javafx.scene.control.TextField
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import kotlin.random.Random
import tornadofx.*
import kotlin.math.absoluteValue

class VertexView(
    val vertex: Vertex
): Circle() {

    val controller = VertexController()

    var visibleText: BooleanProperty = SimpleBooleanProperty(false)

    init {
        radius = vertex.layoutData.radius
        centerX = vertex.layoutData.delta.x
        centerY = vertex.layoutData.delta.y
        fill = Color.RED
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
    //TODO подумать над дублирующимся кодом
    inner class VertexEditor(
        onDelete: () -> Unit
    ) : Fragment() {
        override val root: Parent = vbox {
            TextField(vertex.value).apply {
                textProperty().addListener { _, _, newValue ->
                    if(newValue != "") vertex.value = newValue
                }
            }.also {
                label("Value") {
                    labelFor = it
                }
                add(it)
            }
            button("Delete") {
                action {
                    onDelete()
                }
            }
        }
    }
}