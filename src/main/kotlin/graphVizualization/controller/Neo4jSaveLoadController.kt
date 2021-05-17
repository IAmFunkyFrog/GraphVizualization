package graphVizualization.controller

import graphVizualization.model.Neo4jDBConnection
import graphVizualization.model.Edge
import graphVizualization.model.Graph
import tornadofx.Controller

class Neo4jSaveLoadController : Controller() {

    private val uri = "bolt://localhost:7687"
    private val username = "neo4j"
    private val password = "123"

    fun saveGraph(graph: Graph, name: String) = Neo4jDBConnection(uri, username, password).use {
        it.addGraph(graph, name)
    }

    fun getAllGraphNames() = Neo4jDBConnection(uri, username, password).use {
        it.getAllGraphNames()
    }

    fun getGraphByName(name: String): Graph = Neo4jDBConnection(uri, username, password).use {
        val vertices = it.getVerticesByGraphName(name).map { v -> v.value to v }.toMap()
        val edges = it.getEdgeValuesByGraphName(name).map { (value1, value2, weight) ->
            val vertex1 = vertices[value1] ?: throw IllegalArgumentException()
            val vertex2 = vertices[value2] ?: throw IllegalArgumentException()
            Edge(vertex1, vertex2, weight)
        }.filter { e -> e.vertex1.value < e.vertex2.value }
        return Graph(vertices.values as MutableCollection, edges as MutableList)
    }
}