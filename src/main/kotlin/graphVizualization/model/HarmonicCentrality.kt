package graphVizualization.model

import Vertex
import java.lang.Exception
import java.util.concurrent.Executors
import java.util.concurrent.Future

class HarmonicCentrality(graph: Graph) {
    private val vertexCentralities = graph.vertices().map { VertexCentrality(it, 0.0) }
    private val matrix: List<MutableList<Double>> =
        graph.vertices().map { graph.vertices().map { Double.POSITIVE_INFINITY }.toMutableList() }
    private val tempMatrix: List<MutableList<Double>> =
        graph.vertices().map { graph.vertices().map { Double.POSITIVE_INFINITY }.toMutableList() }

    init {
        val vertexToIndexMap: Map<Vertex, Int> = graph.vertices().mapIndexed { i, v -> v to i }.toMap()
        graph.edges().forEach {
            /*matrix[vertexToIndexMap[it.vertex1]!!][vertexToIndexMap[it.vertex2]!!] = it.weight
            matrix[vertexToIndexMap[it.vertex2]!!][vertexToIndexMap[it.vertex1]!!] = it.weight*/
            tempMatrix[vertexToIndexMap[it.vertex1]!!][vertexToIndexMap[it.vertex2]!!] = it.weight
            tempMatrix[vertexToIndexMap[it.vertex2]!!][vertexToIndexMap[it.vertex1]!!] = it.weight
        }
        /*matrix.indices.forEach { i ->
            matrix[i][i] = 0.0
        }*/
        tempMatrix.indices.forEach { i ->
            tempMatrix[i][i] = 0.0
        }
    }


    fun run() {
        /*vertexCentralities.indices.forEach { k ->
            vertexCentralities.indices.forEach { m ->
                vertexCentralities.indices.forEach { j ->
                    matrix[m][j] = minOf(matrix[m][j], matrix[m][k] + matrix[k][j])
                }
            }
        }*/
        fun minDistance(dist: DoubleArray, used: BooleanArray): Int {
            var min = Double.POSITIVE_INFINITY
            var minIndex = -1
            vertexCentralities.indices.forEach { v ->
                if (!used[v] && dist[v] <= min) {
                    min = dist[v]
                    minIndex = v
                }
            }
            return minIndex
        }

        vertexCentralities.indices.forEach { k ->
            val used = BooleanArray(vertexCentralities.size) { false }
            val dist = DoubleArray(vertexCentralities.size) { Double.POSITIVE_INFINITY }
            dist[k] = 0.0
            used[k] = true
            vertexCentralities.indices.forEach { v ->
                if (!used[v] && tempMatrix[k][v] != 0.0 && dist[k] < Double.POSITIVE_INFINITY && dist[k] + tempMatrix[k][v] < dist[v])
                    dist[v] = dist[k] + tempMatrix[k][v]
            }
            vertexCentralities.indices.forEach {
                val u = minDistance(dist, used)
                if (u != -1) {
                    used[u] = true
                    vertexCentralities.indices.forEach { v ->
                        if (!used[v] && tempMatrix[u][v] != 0.0 && dist[u] < Double.POSITIVE_INFINITY && dist[u] + tempMatrix[u][v] < dist[v])
                            dist[v] = dist[u] + tempMatrix[u][v]
                    }
                }

            }
            dist.indices.forEach { i ->
                matrix[k][i] = dist[i]
            }
        }

        vertexCentralities.indices.forEach { i ->
            var sum = 0.0
            vertexCentralities.indices.forEach { j ->
                if (i != j) sum += 1.0 / matrix[i][j]
            }
            vertexCentralities[i].centrality = sum / (vertexCentralities.size - 1)
        }
    }

    fun updateCentrality() {
        vertexCentralities.forEach { it.vertex.centrality = it.centrality }
    }
}

class VertexCentrality(val vertex: Vertex, var centrality: Double)
