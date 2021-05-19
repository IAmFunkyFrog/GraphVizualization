package graphVizualization.model

import Vertex

class Louvain(val graph: Graph) {
    private var adjacencyMap: Map<Vertex, MutableMap<Vertex, Edge> > = graph.getVertices().associateWith { mutableMapOf() }
    private var vertices: List<Vertex> = graph.getVertices().toList()
    private var currentVertices: List<Vertex>
    private var indexMap: Map<Vertex, Int>
    var modularityIsImproved = false
    private set
    private var currentPartition: IntArray
    private var resultPartition: IntArray
    private var totalCommunityWeight: DoubleArray
    private var innerCommunityWeight: DoubleArray
    private var verticesDegree: DoubleArray
    private var verticesSelfLoops: DoubleArray
    private var totalGraphWeight: Double = graph.getEdges().sumOf { it.weight }
    private var numberOfCommunities = vertices.size

    init {
        indexMap = vertices.indices.associateBy { vertices[it] }
        currentVertices = vertices
        verticesDegree = DoubleArray(vertices.size) { 0.0 }
        verticesSelfLoops = DoubleArray(vertices.size) { 0.0 }
        totalGraphWeight = graph.getEdges().sumOf { it.weight }
        numberOfCommunities = vertices.size
        for (edge in graph.getEdges()) {
            verticesDegree[indexMap[edge.vertex1]!!] += edge.weight
            if (edge.vertex1 != edge.vertex2) verticesDegree[indexMap[edge.vertex2]!!] += edge.weight
            adjacencyMap[edge.vertex1]!![edge.vertex2] = Edge(edge.vertex1, edge.vertex2, edge.weight +
                    (adjacencyMap[edge.vertex1]?.get(edge.vertex1)?.weight ?: 0.0))
            if (edge.vertex1 != edge.vertex2)
                adjacencyMap[edge.vertex2]!![edge.vertex1] = Edge(edge.vertex2, edge.vertex1, edge.weight +
                        (adjacencyMap[edge.vertex2]?.get(edge.vertex1)?.weight ?: 0.0))
            else verticesSelfLoops[indexMap[edge.vertex1]!!] += edge.weight
        }
        currentPartition = IntArray(vertices.size) { it }
        resultPartition = IntArray(vertices.size) { it }
        totalCommunityWeight = DoubleArray(vertices.size) { verticesDegree[it] }
        innerCommunityWeight = DoubleArray(vertices.size) {
            val vertex = vertices[it]
            adjacencyMap[vertex]?.get(vertex)?.weight ?: 0.0
        }
    }

    private fun removeVertexFromCommunity(vertex: Vertex, weightFromNodeToCommunity: Double) {
        val vertexIndex = indexMap[vertex]!!
        val previousCommunity = currentPartition[vertexIndex]
        totalCommunityWeight[previousCommunity] -= verticesDegree[vertexIndex]
        innerCommunityWeight[previousCommunity] -= 2 * weightFromNodeToCommunity +
                verticesSelfLoops[vertexIndex]
        currentPartition[vertexIndex] = -1
    }

    private fun insertVertexToCommunity(vertex: Vertex, community: Int, weightFromNodeToCommunity: Double) {
        val vertexIndex = indexMap[vertex]!!
        val previousCommunity = currentPartition[vertexIndex]
        if (previousCommunity == community) return
        totalCommunityWeight[community] += verticesDegree[vertexIndex]
        innerCommunityWeight[community] += 2 * weightFromNodeToCommunity + verticesSelfLoops[vertexIndex]
        currentPartition[vertexIndex] = community
    }

    private fun computeModularityGainOnInsertVertexToCommunity(vertex: Vertex, community: Int,
                                                               weightFromNodeToCommunity: Double): Double {
        val vertexIndex = indexMap[vertex]!!
        return (weightFromNodeToCommunity - totalCommunityWeight[community] * verticesDegree[vertexIndex] /
                (2 * totalGraphWeight))
    }

    private fun improveModularity() {
        modularityIsImproved = false
        for (vertex in currentVertices.shuffled()) {
            var bestCommunity = currentPartition[indexMap[vertex]!!]
            val weightFromNodeToCommunities = DoubleArray(currentVertices.size) { 0.0 }
            for (edge in adjacencyMap[vertex]!!.values) {
                val currentCommunity = currentPartition[indexMap[edge.vertex2]!!]
                weightFromNodeToCommunities[currentCommunity] += edge.weight
            }
            removeVertexFromCommunity(vertex, weightFromNodeToCommunities[bestCommunity])
            var bestModularityGain = computeModularityGainOnInsertVertexToCommunity(vertex, bestCommunity,
                weightFromNodeToCommunities[bestCommunity])
            for (edge in adjacencyMap[vertex]!!.values) {
                val currentCommunity = currentPartition[indexMap[edge.vertex2]!!]
                val modularityGain = if (currentCommunity != -1) computeModularityGainOnInsertVertexToCommunity(vertex, currentCommunity,
                    weightFromNodeToCommunities[currentCommunity]) else 0.0
                if (modularityGain > bestModularityGain) {
                    bestModularityGain = modularityGain
                    bestCommunity = currentCommunity
                    modularityIsImproved = true
                }
            }
            insertVertexToCommunity(vertex, bestCommunity, weightFromNodeToCommunities[bestCommunity])
        }
    }

    private fun renumberCommunities() {
        val oldCommunityIndicesToNew = IntArray(currentPartition.size) { -1 }
        for (community in currentPartition) {
            oldCommunityIndicesToNew[community] = 0
        }
        var numberOfCommunities = 0
        for (i in oldCommunityIndicesToNew.indices) {
            if (oldCommunityIndicesToNew[i] != -1) {
                oldCommunityIndicesToNew[i] = numberOfCommunities
                numberOfCommunities++
            }
        }
        this.numberOfCommunities = numberOfCommunities
        val newCommunityIndicesToOld = IntArray(numberOfCommunities) { 0 }
        for (i in oldCommunityIndicesToNew.indices) {
            if (oldCommunityIndicesToNew[i] != -1) {
                newCommunityIndicesToOld[oldCommunityIndicesToNew[i]] = i
            }
        }
        for (i in currentPartition.indices) {
            if (oldCommunityIndicesToNew[i] == -1) oldCommunityIndicesToNew[i] = oldCommunityIndicesToNew[currentPartition[i]]
        }
        innerCommunityWeight = DoubleArray(numberOfCommunities) { innerCommunityWeight[newCommunityIndicesToOld[it]] }
        totalCommunityWeight = DoubleArray(numberOfCommunities) { totalCommunityWeight[newCommunityIndicesToOld[it]] }
        for (vertexIndex in resultPartition.indices) {
            resultPartition[vertexIndex] = oldCommunityIndicesToNew[resultPartition[vertexIndex]]
        }
        for (vertexIndex in currentPartition.indices) {
            currentPartition[vertexIndex] = oldCommunityIndicesToNew[currentPartition[vertexIndex]]
        }
    }

    private fun rebuildGraph() {
        renumberCommunities()
        val newVertices = Array(numberOfCommunities) { Vertex("") }
        for (vertexIndex in currentVertices.indices) {
            newVertices[currentPartition[vertexIndex]] = currentVertices[vertexIndex]
        }
        verticesSelfLoops = DoubleArray(newVertices.size) { innerCommunityWeight[it] }
        verticesDegree = DoubleArray(newVertices.size) { totalCommunityWeight[it] }
        innerCommunityWeight = DoubleArray(newVertices.size) { 0.0 }
        totalCommunityWeight = DoubleArray(newVertices.size) { 0.0 }
        val newAdjacencyMap: Map<Vertex, MutableMap <Vertex, Edge> > = newVertices.associateWith { mutableMapOf() }
        for (vertexIndex in newVertices.indices) {
            val vertex = newVertices[vertexIndex]
            for (entry in adjacencyMap[vertex]!!.entries) {
                val community = currentPartition[indexMap[entry.key]!!]
                val vertexOfCommunity = newVertices[community]
                newAdjacencyMap[vertex]!![vertexOfCommunity] = Edge(
                    vertex, vertexOfCommunity,
                    entry.value.weight + (newAdjacencyMap[vertex]!![vertexOfCommunity]?.weight ?: 0.0)
                )
                if (community == vertexIndex) innerCommunityWeight[community] += entry.value.weight
                totalCommunityWeight[community] += entry.value.weight
            }
        }
        indexMap = newVertices.indices.associateBy { newVertices[it] }
        currentPartition = IntArray(newVertices.size) { it }
        currentVertices = newVertices.toList()
        adjacencyMap = newAdjacencyMap
    }

    fun doIteration() {
        var someCommunitiesAreMerged = false
        do {
            improveModularity()
            if (modularityIsImproved) someCommunitiesAreMerged = true
        } while (modularityIsImproved)
        rebuildGraph()
        if (someCommunitiesAreMerged) modularityIsImproved = true
        for (vertexIndex in vertices.indices) {
            vertices[vertexIndex].community = resultPartition[vertexIndex]
        }
    }
}
