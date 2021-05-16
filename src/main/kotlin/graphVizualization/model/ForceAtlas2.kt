package graphVizualization.model

import Vertex
import javafx.geometry.Point2D
import kotlin.math.pow

val Vertex.traction
    get() = layoutData.appliedForce.add(layoutData.oldAppliedForce).multiply(0.5).magnitude()

val Vertex.swing
    get() = layoutData.appliedForce.add(layoutData.oldAppliedForce.multiply(-1.0)).multiply(0.5)
        .magnitude()

class ForceAtlas2(
    private var graph: Graph,
) {
    var vertices = graph.vertices()
    var edges = graph.edges()
    
    var attraction: Force = Force.Factory.LinLogAttraction()
    var repulsion: Force = Force.Factory.DistanceRepulsion()
    var gravity: Force = Force.Factory.DefaultGravity()

    private lateinit var rootRegion: BurnsHutRegion

    var repulsionCoefficient
        get() = repulsion.coefficient
        set(value) {
            repulsion.coefficient = value
        }

    var gravityCoefficient
        get() = gravity.coefficient
        set(value) {
            gravity.coefficient = value
        }

    var burnsHutTheta = 1.8
        set(value) {
            field = value
            if (this::rootRegion.isInitialized) rootRegion.theta = value
        }

    var speedCoefficient = 0.5
    var maxSpeedCoefficient = 10.0
    var toleranceCoefficient = 1.0
    var burnsHut = false
    var preventOverlapping = true
    var dissuadeHubs = false
    var overlappingCoefficient = 100.0
    var edgeWeightCoefficient = 0.0

    private val vCenter = Point2D(
        vertices.fold(0.0) { acc, v -> acc + v.layoutData.delta.x / vertices.size },
        vertices.fold(0.0) { acc, v -> acc + v.layoutData.delta.y / vertices.size }
    )

    fun doIteration() {
        //Preparation of vertices
        for (vertex in vertices) vertex.layoutData.prepareToIteration()
        if (burnsHut) rootRegion = BurnsHutRegion(vertices, burnsHutTheta)
        //Repulsion
        if (burnsHut) {
            for (vertex in vertices) repulsion.applyForce(rootRegion, vertex, this::repulsionAdditionalCoefficient)
        } else {
            for (i in vertices.indices) {
                val vertex1 = vertices[i]
                for (j in i + 1 until vertices.size) {
                    val vertex2 = vertices[j]
                    repulsion.applyForce(vertex1, vertex2, this::repulsionAdditionalCoefficient)
                    repulsion.applyForce(vertex2, vertex1, this::repulsionAdditionalCoefficient)
                }
            }
        }
        //Attraction
        for (edge in edges) {
            val weight = if(edgeWeightCoefficient == 0.0) 1.0 else (edge.weight).pow(edgeWeightCoefficient)
            val preventOverlappingValue = attractionAdditionalCoefficient(edge.vertex1, edge.vertex2)
            attraction.applyForce(edge.vertex1, edge.vertex2) { v1, _ ->
                if(dissuadeHubs) preventOverlappingValue * weight / (v1.degree + 1)
                else preventOverlappingValue * weight
            }
            attraction.applyForce(edge.vertex2, edge.vertex1) { v1, _ ->
                if(dissuadeHubs) preventOverlappingValue * weight / (v1.degree + 1)
                else preventOverlappingValue * weight
            }
        }

        //Gravity
        for (vertex in vertices) {
            gravity.applyForce(vCenter, vertex)
        }
        //Global speed computation
        val (swing, traction) = vertices.fold(Pair(0.0, 0.0)) { acc, v ->
            Pair(acc.first + (v.degree + 1) * v.swing, acc.second + (v.degree + 1) * v.traction)
        }
        val globalSpeed = toleranceCoefficient * traction / swing
        //Displacement of vertices computation
        for (vertex in vertices) {
            val speed = minOf(
                speedCoefficient * globalSpeed / (1.0 + globalSpeed * swing.pow(0.5)),
                maxSpeedCoefficient / vertex.layoutData.appliedForce.magnitude()
            )

            vertex.layoutData.delta = vertex.layoutData.delta.add(vertex.layoutData.appliedForce.multiply(speed))
        }
    }

    fun reset(graph: Graph) {
        this.graph = graph
        vertices = graph.vertices()
        edges = graph.edges()
    }

    private fun checkOverlapping(v1: Vertex, v2: Vertex): Boolean {
        return v1.layoutData.delta.distance(v2.layoutData.delta) < v1.layoutData.radius + v2.layoutData.radius
    }

    private fun repulsionAdditionalCoefficient(v1: Vertex, v2: Vertex): Double {
        return if(preventOverlapping && checkOverlapping(v1, v2)) overlappingCoefficient
        else 1.0
    }

    private fun attractionAdditionalCoefficient(v1: Vertex, v2: Vertex): Double {
        return if(preventOverlapping && checkOverlapping(v1, v2)) 0.0
        else 1.0
    }
}