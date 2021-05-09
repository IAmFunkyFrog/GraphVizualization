package graphVizualization.controller

import graphVizualization.model.Graph
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.GraphDatabase
import tornadofx.Controller
import java.io.Closeable

class Neo4jSaveLoadController : Controller() {

    fun saveGraph(graph: Graph, name: String) = DBConnection().use {
        it.addGraph(graph, name)
    }

    fun getAllGraphs() = DBConnection().use {
        it.getAllGraphs()
    }

    fun getGraphByName(name: String) = DBConnection().use {
        //TODO сделать метод
    }

    private inner class DBConnection : Closeable {

        private val uri = "bolt://localhost:7687"
        private val username = "neo4j"
        private val password = "9821lun23"

        private val driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password))
        private val session = driver.session()

        fun addGraph(graph: Graph, name: String) {
            session.writeTransaction {
                try {
                    it.run("MERGE (:Graph {name: \$name})", mutableMapOf("name" to name) as Map<String, Any>?)
                    it.run(
                        "MATCH (g:Graph {name: \$name}), (v:Vertex)-[:IN]-(g) DETACH DELETE v",
                        mutableMapOf(
                            "name" to name
                        ) as Map<String, Any>?
                    )
                    for (vertex in graph.vertices()) it.run(
                        "MATCH (g:Graph {name: \$name}) MERGE (v:Vertex {value: \$value})-[:IN]->(g)",
                        mutableMapOf(
                            "name" to name,
                            "value" to vertex.value
                        ) as Map<String, Any>?
                    )
                    for (edge in graph.edges()) it.run(
                        "MATCH (g:Graph {name: \$name}) MERGE (v1:Vertex {value: \$value1})-[:IN]->(g) MERGE (v2:Vertex {value: \$value2})-[:IN]->(g) MERGE (v1)-[:CONNECTED {weight: \$weight}]-(v2)",
                        mutableMapOf(
                            "name" to name,
                            "value1" to edge.vertex1.value,
                            "value2" to edge.vertex2.value,
                            "weight" to edge.weight
                        ) as Map<String, Any>?
                    )
                } catch (e: Exception) {
                    //TODO подумать над включением логера в проект
                    println(e.message)
                }
            }
        }

        fun getAllGraphs() = mutableListOf<String>().apply {
            session.writeTransaction {
                try {
                    val graphs = it.run("MATCH (g:Graph) RETURN g.name AS name")
                    for(graph in graphs) {
                        println(graph)
                        this.add(graph.get("name").asString())
                    }
                }
                catch(e: Exception) {
                    //TODO подумать над включением логера в проект
                    println(e.message)
                }
            }
        }

        fun getVerticesByGraphName() {

        }

        override fun close() {
            session.close()
            driver.close()
        }
    }
}