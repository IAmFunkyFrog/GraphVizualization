package graphVizualization.view

import graphVizualization.model.Edge
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.Line

class EdgeView(
    val edge: Edge,
    val vertexView1: VertexView,
    val vertexView2: VertexView
) : Line() {

    init {
        startXProperty().bind(vertexView1.centerXProperty())
        startYProperty().bind(vertexView1.centerYProperty())
        endXProperty().bind(vertexView2.centerXProperty())
        endYProperty().bind(vertexView2.centerYProperty())
    }

}