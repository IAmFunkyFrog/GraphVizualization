package graphApp.view

import graphApp.model.Edge
import javafx.scene.shape.Line

class EdgeView<V>(
    val edge: Edge<V>,
    val vertexView1: VertexView<V>,
    val vertexView2: VertexView<V>
) : Line() {

    init {
        startXProperty().bind(vertexView1.centerXProperty())
        startYProperty().bind(vertexView1.centerYProperty())
        endXProperty().bind(vertexView2.centerXProperty())
        endYProperty().bind(vertexView2.centerYProperty())
    }

}