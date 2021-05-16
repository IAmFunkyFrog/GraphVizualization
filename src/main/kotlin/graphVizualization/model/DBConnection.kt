package graphVizualization.model

import Vertex
import javafx.geometry.Point2D
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.GraphDatabase
import java.io.Closeable

class DBConnection(
    uri: String,
    username: String,
    password: String
) : Closeable {

    private val driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password))
    private val session = driver.session()

    fun addGraph(graph: Graph, name: String) {
        session.writeTransaction {
            it.run("MERGE (:Graph {name: \$name})", mutableMapOf("name" to name) as Map<String, Any>?)
            it.run(
                "MATCH (g:Graph {name: \$name}), (v:Vertex)-[:IN]->(g) DETACH DELETE v",
                mutableMapOf(
                    "name" to name
                ) as Map<String, Any>?
            )
            for (vertex in graph.vertices()) it.run(
                "MATCH (g:Graph {name: \$name}) MERGE (v:Vertex {value: \$value, centerX: \$centerX, centerY: \$centerY, centrality: \$centrality})-[:IN]->(g)",
                mutableMapOf(
                    "name" to name,
                    "value" to vertex.value,
                    "centerX" to vertex.layoutData.delta.x,
                    "centerY" to vertex.layoutData.delta.y,
                    "centrality" to vertex.centrality
                ) as Map<String, Any>?
            )
            for (edge in graph.edges()) it.run(
                "MATCH (g:Graph {name: \$name}) MERGE (v1:Vertex {value: \$value1, centerX: \$centerX1, centerY: \$centerY1, centrality: \$centrality1})-[:IN]->(g) MERGE (v2:Vertex {value: \$value2, centerX: \$centerX2, centerY: \$centerY2, centrality: \$centrality2})-[:IN]->(g) MERGE (v1)-[:CONNECTED {weight: \$weight}]-(v2)",
                mutableMapOf(
                    "name" to name,
                    "value1" to edge.vertex1.value,
                    "value2" to edge.vertex2.value,
                    "centerX1" to edge.vertex1.layoutData.delta.x,
                    "centerX2" to edge.vertex2.layoutData.delta.x,
                    "centerY1" to edge.vertex1.layoutData.delta.y,
                    "centerY2" to edge.vertex2.layoutData.delta.y,
                    "centrality1" to edge.vertex1.centrality,
                    "centrality2" to edge.vertex2.centrality,
                    "weight" to edge.weight
                ) as Map<String, Any>?
            )
        }
    }

    fun getAllGraphNames() = mutableListOf<String>().apply {
        session.writeTransaction {
            val graphNames = it.run("MATCH (g:Graph) RETURN g.name AS name")
            for (graph in graphNames) this.add(graph.get("name").asString())
        }
    }

    fun getVerticesByGraphName(name: String) = mutableListOf<Vertex>().apply {
        session.writeTransaction { tx ->
            val vertexValues = tx.run(
                "MATCH (g:Graph {name: \$name}), (v:Vertex)-[:IN]-(g) RETURN v.value AS value, v.centerX AS centerX, v.centerY AS centerY, v.centrality AS centrality",
                mutableMapOf(
                    "name" to name
                ) as Map<String, Any>?
            )
            for (vertex in vertexValues) {
                this.add(Vertex(vertex.get("value").asString()).apply {
                    layoutData.delta = Point2D(vertex.get("centerX").asDouble(), vertex.get("centerY").asDouble())
                    centrality = vertex.get("centrality").asDouble()
                })
            }
        }
    }

    fun getEdgeValuesByGraphName(name: String) = mutableListOf<Triple<String, String, Double>>().apply {
        session.writeTransaction { tx ->
            val edgeValues = tx.run(
                "MATCH (g:Graph {name: \$name}), (v1:Vertex)-[:IN]-(g), (v1)-[rel:CONNECTED]-(v2:Vertex) RETURN rel.weight AS weight, v1.value AS value1, v2.value AS value2",
                mutableMapOf(
                    "name" to name
                ) as Map<String, Any>?
            )
            for (edge in edgeValues) {
                val weight = edge.get("weight").asDouble()
                val vertex1 = edge.get("value1").asString()
                val vertex2 = edge.get("value2").asString()
                this.add(Triple(vertex1, vertex2, weight))
            }
        }
    }

    override fun close() {
        session.close()
        driver.close()
    }
}