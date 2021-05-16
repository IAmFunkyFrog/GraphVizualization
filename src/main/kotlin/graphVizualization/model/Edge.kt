package graphVizualization.model

import Vertex

class Edge(
    val vertex1: Vertex,
    val vertex2: Vertex,
    var weight: Double
): Comparable<Edge> {
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

    override fun compareTo(other: Edge): Int {
        return if(vertex1.compareTo(other.vertex1) == 0 && vertex2.compareTo(other.vertex2) == 0 ||
            vertex1.compareTo(other.vertex2) == 0 && vertex2.compareTo(other.vertex1) == 0) 0
        else vertex1.compareTo(other.vertex1)
    }

}