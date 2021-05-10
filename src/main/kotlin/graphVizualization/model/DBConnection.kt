package graphVizualization.model

import org.neo4j.driver.AuthTokens
import org.neo4j.driver.GraphDatabase
import java.io.Closeable

class DBConnection(
    uri: String,
    username: String,
    password: String
): Closeable {

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

    fun getAllGraphNames() = mutableListOf<String>().apply {
        session.writeTransaction {
            try {
                val graphNames = it.run("MATCH (g:Graph) RETURN g.name AS name")
                for (graph in graphNames) this.add(graph.get("name").asString())
            } catch (e: Exception) {
                //TODO подумать над включением логера в проект
                println(e.message)
            }
        }
    }

    fun getVertexValuesByGraphName(name: String) = mutableListOf<String>().apply {
        session.writeTransaction { tx ->
            try {
                val vertexValues = tx.run(
                    "MATCH (g:Graph {name: \$name}), (v:Vertex)-[:IN]-(g) RETURN v.value AS value",
                    mutableMapOf(
                        "name" to name
                    ) as Map<String, Any>?
                )
                for(vertex in vertexValues) vertex.get("value").asString().also {
                    this.add(it)
                }
            } catch (e: Exception) {
                //TODO подумать над включением логера в проект
                println(e.message)
            }
        }
    }

    fun getEdgeValuesByGraphName(name: String) = mutableListOf<Triple<String, String, Double>>().apply {
        session.writeTransaction { tx ->
            try {
                val edgeValues = tx.run(
                    "MATCH (g:Graph {name: \$name}), (v1:Vertex)-[:IN]-(g), (v1)-[rel:CONNECTED]-(v2:Vertex) RETURN rel.weight AS weight, v1.value AS value1, v2.value AS value2",
                    mutableMapOf(
                        "name" to name
                    ) as Map<String, Any>?
                )
                for(edge in edgeValues) {
                    val weight = edge.get("weight").asDouble()
                    val vertex1 = edge.get("value1").asString()
                    val vertex2 = edge.get("value2").asString()
                    this.add(Triple(vertex1, vertex2, weight))
                }
            } catch (e: Exception) {
                //TODO подумать над включением логера в проект
                println(e.message)
            }
        }
    }

    override fun close() {
        session.close()
        driver.close()
    }
}