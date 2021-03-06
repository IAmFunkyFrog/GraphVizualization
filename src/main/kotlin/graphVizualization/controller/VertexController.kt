package graphVizualization.controller

import graphVizualization.view.VertexView
import javafx.geometry.Point2D
import javafx.scene.input.MouseEvent
import tornadofx.Controller

class VertexController: Controller() {

    fun onDrag(e: MouseEvent, vertexView: VertexView) {
        if(e.isControlDown) return

        vertexView.centerX = e.x
        vertexView.centerY = e.y
        vertexView.vertex.layoutData.delta = Point2D(e.x, e.y)
    }

    fun onMouseEntered(vertexView: VertexView) {
        vertexView.visibleText.value = true
    }

    fun onMouseExited(vertexView: VertexView) {
        vertexView.visibleText.value = false
    }

    fun onPress(e: MouseEvent, vertexView: VertexView, createEdge: (VertexView, VertexView) -> Unit) {
        if (!e.isSecondaryButtonDown) {
            pressedVertex = null
            return
        }

        if (pressedVertex == null) pressedVertex = vertexView
        else pressedVertex?.let {
            pressedVertex = null
            createEdge(it, vertexView)
        }
    }

    companion object {
        var pressedVertex: VertexView? = null
    }
}