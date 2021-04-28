package graphApp.model

import Vertex

class Edge<V>(
    val vertex1: Vertex<V>,
    val vertex2: Vertex<V>,
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