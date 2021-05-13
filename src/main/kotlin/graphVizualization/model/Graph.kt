package graphVizualization.model

import Vertex
import kotlin.math.absoluteValue
import kotlin.random.Random

class Graph(
    val vertices: MutableCollection<Vertex>,
    val edges: MutableCollection<Edge>,
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

        fun ControlGraph(n: Int, pCount: Int): Graph {
            val graph = Graph(ArrayList(), ArrayList())

            for(i in 1..n) graph.vertices.add(Vertex(i.toString()))

            val vertices = graph.vertices()
            val part = vertices.size / pCount
            val edgesCount = n
            for(i in 1..edgesCount) {
                val v1 = Random.nextInt().absoluteValue % part
                val v2 = Random.nextInt().absoluteValue % part
                for(j in 0 until pCount) graph.addEdge(Edge(vertices[v1 + part * j], vertices[v2 + part * j], 1.0))
            }

            for(j in 0 until pCount - 1) graph.addEdge(Edge(vertices[part * j], vertices[part * (j + 1)], 1.0))

            return graph
        }

        fun RandomGraph(n: Int): Graph {
            val graph = Graph(ArrayList(), ArrayList())

            for(i in 1..n) graph.vertices.add(Vertex(i.toString()))

            val edgesCount = n
            val vertices = graph.vertices()
            for(i in 1..edgesCount) {
                val v1 = Random.nextInt().absoluteValue % vertices.size
                val v2 = Random.nextInt().absoluteValue % vertices.size
                graph.addEdge(Edge(vertices[v1], vertices[v2], Random.nextDouble()))
            }

            return graph
        }

        fun FullGraph(): Graph {
            val graph = Graph(ArrayList(), ArrayList())

            for(i in 1..250) graph.addVertex(Vertex(i.toString()))

            val vertices = graph.vertices()
            for(i in 0..249) {
                for(j in i+1..249) {
                    graph.addEdge(Edge(vertices[i], vertices[j], 1.0))
                }
            }

            return graph
        }

    }
}