package graphVizualization.controller

import graphVizualization.view.VertexView
import javafx.scene.input.MouseEvent
import tornadofx.Controller

class VertexController: Controller() {

    fun onDrag(e: MouseEvent, vertexView: VertexView) {
        if(e.isControlDown) return

        vertexView.centerX = e.x
        vertexView.centerY = e.y
    }

    fun onMouseEntered(vertexView: VertexView) {
        vertexView.visibleText.value = true
    }

    fun onMouseExited(vertexView: VertexView) {
        vertexView.visibleText.value = false
    }
}