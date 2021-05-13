package graphVizualization.model

import Vertex

class HarmonicCentrality(graph: Graph) {
    private val vertexCentralities = graph.vertices().map { VertexCentrality(it, 0.0) }
    private val matrix: List<MutableList<Double>> =
        graph.vertices().map { graph.vertices().map { Double.POSITIVE_INFINITY }.toMutableList() }

    init {
        val vertexToIndexMap: Map<Vertex, Int> = graph.vertices().mapIndexed { i, v -> v to i }.toMap()
        graph.edges().forEach {
            matrix[vertexToIndexMap[it.vertex1]!!][vertexToIndexMap[it.vertex2]!!] = it.weight
            matrix[vertexToIndexMap[it.vertex2]!!][vertexToIndexMap[it.vertex1]!!] = it.weight
        }
        matrix.indices.forEach { i ->
            matrix[i][i] = 0.0
        }

    }

    fun run() {
        vertexCentralities.indices.forEach { k ->
            vertexCentralities.indices.forEach { m ->
                vertexCentralities.indices.forEach { j ->
                    matrix[m][j] = minOf(matrix[m][j], matrix[m][k] + matrix[k][j])
                }
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
