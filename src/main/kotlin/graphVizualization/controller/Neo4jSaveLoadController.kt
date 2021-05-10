package graphVizualization.controller

import Vertex
import graphVizualization.model.DBConnection
import graphVizualization.model.Edge
import graphVizualization.model.Graph
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.GraphDatabase
import tornadofx.Controller
import java.io.Closeable

class Neo4jSaveLoadController : Controller() {

    private val uri = "bolt://localhost:7687"
    private val username = "neo4j"
    private val password = "9821lun23"

    fun saveGraph(graph: Graph, name: String) = DBConnection(uri, username, password).use {
        it.addGraph(graph, name)
    }

    fun getAllGraphNames() = DBConnection(uri, username, password).use {
        it.getAllGraphNames()
    }

    fun getGraphByName(name: String): Graph = DBConnection(uri, username, password).use {
        val vertices = it.getVertexValuesByGraphName(name).map { value -> value to Vertex(value) }.toMap()
        val edges = it.getEdgeValuesByGraphName(name).map { (value1, value2, weight) ->
            val vertex1 = vertices[value1] ?: throw IllegalArgumentException()
            val vertex2 = vertices[value2] ?: throw IllegalArgumentException()
            Edge(vertex1, vertex2, weight)
        }.filter { e -> e.vertex1.value < e.vertex2.value }
        return Graph(vertices.values as MutableCollection, edges as MutableList)
    }
}