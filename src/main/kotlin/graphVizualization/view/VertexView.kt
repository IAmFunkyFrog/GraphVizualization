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
import javafx.scene.text.Font
import tornadofx.*

class VertexView(
    val vertex: Vertex
): Circle() {

    val controller = VertexController()

    var visibleText: BooleanProperty = SimpleBooleanProperty(false)

    val label = text {
        text = vertex.value
        fill = Color.BLUE
        xProperty().bind(centerXProperty() + radiusProperty())
        yProperty().bind(centerYProperty() - radiusProperty())
        visibleWhen(visibleText)
    }

    init {
        radius = vertex.layoutData.radius
        radiusProperty().addListener { _, _, newValue ->
            label.font = Font(newValue.toDouble())
        }
        centerX = vertex.layoutData.delta.x
        centerY = vertex.layoutData.delta.y
        fill = Color.RED
    }

    fun applyDisplacement(displacement: Point2D) {
        centerX += displacement.x
        centerY += displacement.y
    }

    inner class VertexEditor(
        onDelete: () -> Unit
    ) : Fragment() {
        override val root: Parent = vbox {
            TextField(vertex.value).apply {
                textProperty().addListener { _, _, newValue ->
                    if(newValue != "") {
                        vertex.value = newValue
                        label.text = newValue
                    }
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