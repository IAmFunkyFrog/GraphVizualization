package graphVizualization.model

import Vertex

class Edge(
    val vertex1: Vertex,
    val vertex2: Vertex,
    var weight: Double
) {
    private var enabled = false

    init {
        enable()
    }

    fun enable() {
        vertex1.onEdgeCreated()
        vertex2.onEdgeCreated()
        enabled = true
    }

    fun disable() {
        vertex1.onEdgeDeleted()
        vertex2.onEdgeDeleted()
        enabled = false
    }

}