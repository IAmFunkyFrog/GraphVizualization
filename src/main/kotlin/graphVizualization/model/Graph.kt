package graphVizualization.model

import Vertex
import kotlin.math.absoluteValue
import kotlin.random.Random

class Graph(
    private val vertices: MutableCollection<Vertex>,
    private val edges: MutableCollection<Edge>,
) {

    fun addVertex(vertex: Vertex): Vertex? {
        return if(vertices.any { v -> v == vertex }) null
        else vertex.also {
            vertices.add(vertex)
        }
    }

    fun addEdge(edge: Edge): Edge? {
        return if(edge.vertex1 == edge.vertex2 || edges.any { e -> e.vertex1 == edge.vertex1 && e.vertex2 == edge.vertex2 } ||
            edges.any { e -> e.vertex1 == edge.vertex2 && e.vertex2 == edge.vertex1 }) null
        else edge.also {
            edges.add(edge)
        }
    }

    fun removeVertex(vertex: Vertex) = vertices.remove(vertex)

    fun removeEdge(edge: Edge) = edges.remove(edge)

    fun vertices() = vertices.toList()
    fun edges() = edges.toList()

    companion object {

        fun EmptyGraph() = Graph(ArrayList(), ArrayList())

    }
}