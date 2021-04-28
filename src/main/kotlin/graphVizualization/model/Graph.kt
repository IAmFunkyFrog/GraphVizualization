package graphVizualization.model

import Vertex
import kotlin.math.absoluteValue
import kotlin.random.Random

class Graph<V>(
    private val vertices: MutableCollection<Vertex<V>>,
    private val edges: MutableCollection<Edge<V>>,
) {

    fun addVertex(vertex: Vertex<V>) {
        vertices.add(vertex)
    }

    fun addEdge(edge: Edge<V>) {
        if(edges.any { e -> e.vertex1 == edge.vertex1 && e.vertex2 == edge.vertex2 }) return
        if(edges.any { e -> e.vertex1 == edge.vertex2 && e.vertex2 == edge.vertex1 }) return
        edges.add(edge)
    }

    fun vertices() = vertices.toList()
    fun edges() = edges.toList()

    companion object {

        fun <V> EmptyGraph() = Graph<V>(ArrayList(), ArrayList())

        fun RandomGraph(): Graph<Int> {
            val graph = Graph<Int>(ArrayList(), ArrayList())

            for(i in 1..100) graph.vertices.add(Vertex(i))

            val vertices = graph.vertices()
            for(i in 1..100) {
                val v1 = Random.nextInt().absoluteValue % vertices.size
                val v2 = Random.nextInt().absoluteValue % vertices.size
                graph.addEdge(Edge(vertices[v1], vertices[v2], Random.nextDouble()))
            }

            return graph
        }

        fun FullGraph(): Graph<Int> {
            val graph = Graph<Int>(ArrayList(), ArrayList())

            for(i in 1..250) graph.addVertex(Vertex(i))

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