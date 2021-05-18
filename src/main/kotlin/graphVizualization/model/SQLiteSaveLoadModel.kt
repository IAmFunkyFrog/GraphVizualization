package graphVizualization.model

import Vertex
import javafx.geometry.Point2D
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.sql.Connection

const val MAX_VERTEX_NAME_LENGTH = 128 // FIXME

object Vertices : IdTable<String>() {
    override val id = varchar("id", MAX_VERTEX_NAME_LENGTH).entityId()
    val centrality = double("centrality")
    val centerX = double("centerX")
    val centerY = double("centerY")
    val community = integer("community")
}

object Edges : Table() {
    val vertex1 = reference("vertex1", Vertices)
    val vertex2 = reference("vertex2", Vertices)
    val weight = double("weight")
}

object SQLiteSaveLoadModel {
    fun save(graph: Graph, file: File) {
        Database.connect("jdbc:sqlite:${file.path}", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        transaction {
            SchemaUtils.drop(Vertices, Edges) 
            SchemaUtils.create(Vertices, Edges)
            for (vertex in graph.vertices())
                Vertices.insert {
                    it[id] = vertex.value
                    it[centrality] = vertex.centrality
                    it[centerX] = vertex.layoutData.delta.x
                    it[centerY] = vertex.layoutData.delta.y
                    it[community] = vertex.community
                }
            for (edge in graph.edges())
                Edges.insert {
                    it[vertex1] = edge.vertex1.value
                    it[vertex2] = edge.vertex2.value
                    it[weight] = edge.weight
                }
        }
    }

    fun load(file: File): Graph {
        Database.connect("jdbc:sqlite:${file.path}", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        val vertexValueToVertex = mutableMapOf<String, Vertex>()
        val graph = Graph(mutableListOf(), mutableListOf())
        transaction {
            Vertices.selectAll().forEach { row ->
                val value = row[Vertices.id].value
                val vertex = Vertex(value).apply {
                    layoutData.delta = Point2D(row[Vertices.centerX], row[Vertices.centerY])
                    centrality = row[Vertices.centrality]
                    community = row[Vertices.community]
                }
                graph.addVertex(vertex)
                vertexValueToVertex[value] = vertex
            }
            Edges.selectAll().forEach {
                graph.addEdge(Edge(
                    vertexValueToVertex[it[Edges.vertex1].value]!!,
                    vertexValueToVertex[it[Edges.vertex2].value]!!,
                    it[Edges.weight]
                ))
            }
        }
        return graph
    }
}