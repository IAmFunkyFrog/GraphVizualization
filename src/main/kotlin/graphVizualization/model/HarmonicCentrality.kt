package graphVizualization.model

import Vertex

class HarmonicCentrality(graph: Graph) {
    private val vertexCentralities = graph.vertices().map { VertexCentrality(it, 0.0) }
    private val matrix: List<MutableList<Double>> =
        graph.vertices().map { graph.vertices().map { Double.POSITIVE_INFINITY }.toMutableList() }

    init {
        val vertexToIndexMap: Map<Vertex, Int> = graph.vertices().mapIndexed { i, v -> v to i }.toMap()
        graph.edges().forEach {
            //matrix[vertexToIndexMap[it.vertex1]!!][vertexToIndexMap[it.vertex2]!!] = 1.0 / it.weight
            matrix[vertexToIndexMap[it.vertex1]!!][vertexToIndexMap[it.vertex2]!!] = 1.0
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
        /*vertexCentralities.indices.forEach { k ->
            val used = BooleanArray(vertexCentralities.size) {false}
            val dist = DoubleArray(vertexCentralities.size) {Double.POSITIVE_INFINITY}
            dist[k] = 0.0
            var minIndex:Int
            var min:Double
            do {
                minIndex = 10001
                min = Double.POSITIVE_INFINITY
                vertexCentralities.indices.forEach { i ->
                    if (!used[i] && dist[i] < min) {
                        min = dist[i]
                        minIndex = i
                    }
                }
                if (minIndex != 10001) {
                    vertexCentralities.indices.forEach {i ->
                        if (matrix[minIndex][i] > 0) {
                            if (min + matrix[minIndex][i] < dist[i]) dist[i] = min + matrix[minIndex][i]
                        }
                    }
                    used[minIndex] = true
                }
            } while (minIndex < 10001)
            dist.indices.forEach { i ->
                matrix[k][i] = dist[i]
            }
        }*/
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
