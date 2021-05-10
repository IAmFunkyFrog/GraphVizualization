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
        return if(edges.any { e -> e.vertex1 == edge.vertex1 && e.vertex2 == edge.vertex2 } ||
            edges.any { e -> e.vertex1 == edge.vertex2 && e.vertex2 == edge.vertex1 }) null
        else edge.also {
            edges.add(edge)
        }
    }

    fun vertices() = vertices.toList()
    fun edges() = edges.toList()

    companion object {

        fun EmptyGraph() = Graph(ArrayList(), ArrayList())

        fun ControlGraph(n: Int): Graph {
            val graph = Graph(ArrayList(), ArrayList())

            for(i in 1..n) graph.vertices.add(Vertex(i.toString()))

            val vertices = graph.vertices()
            val part = vertices.size / 6
            val edgesCount = n * 2
            for(i in 1..edgesCount) {
                val v1 = Random.nextInt().absoluteValue % part
                val v2 = Random.nextInt().absoluteValue % part
                graph.addEdge(Edge(vertices[v1], vertices[v2], Random.nextDouble()))
                graph.addEdge(Edge(vertices[v1 + part], vertices[v2 + part], Random.nextDouble()))
                graph.addEdge(Edge(vertices[v1 + part * 2], vertices[v2 + part * 2], Random.nextDouble()))
                graph.addEdge(Edge(vertices[v1 + part * 3], vertices[v2 + part * 3], Random.nextDouble()))
                graph.addEdge(Edge(vertices[v1 + part * 4], vertices[v2 + part * 4], Random.nextDouble()))
                graph.addEdge(Edge(vertices[v1 + part * 5], vertices[v2 + part * 5], Random.nextDouble()))
            }

            graph.addEdge(Edge(vertices[0], vertices[part], Random.nextDouble()))
            graph.addEdge(Edge(vertices[part], vertices[part * 2], Random.nextDouble()))
            graph.addEdge(Edge(vertices[part * 2], vertices[part * 3], Random.nextDouble()))
            graph.addEdge(Edge(vertices[part * 3], vertices[part * 4], Random.nextDouble()))
            graph.addEdge(Edge(vertices[part * 4], vertices[part * 5], Random.nextDouble()))

            return graph
        }

        fun RandomGraph(): Graph {
            val graph = Graph(ArrayList(), ArrayList())

            for(i in 1..100) graph.vertices.add(Vertex(i.toString()))

            val edgesCount = 100
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