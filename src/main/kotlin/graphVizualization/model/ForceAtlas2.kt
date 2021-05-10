package graphVizualization.model

import graphVizualization.view.GraphView
import graphVizualization.view.VertexView
import javafx.geometry.Point2D
import kotlin.math.pow

//TODO: реализовать режим предотвращения коллизий

val VertexView.traction
    get() = vertex.layoutData.appliedForce.add(vertex.layoutData.oldAppliedForce).multiply(0.5).magnitude()

val VertexView.swing
    get() = vertex.layoutData.appliedForce.add(vertex.layoutData.oldAppliedForce.multiply(-1.0)).multiply(0.5)
        .magnitude()

class ForceAtlas2(
    private val graph: GraphView,
) {
    var vertices = graph.vertices.values.toList()
    var edges = graph.edges.values.toList()

    private var attraction = Force.Factory.LinLogAttraction()
    private var repulsion = Force.Factory.DistanceRepulsion()
    private var gravity = Force.Factory.DefaultGravity()

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

    var burnsHutTheta = 1.0
        set(value) {
            field = value
            if (this::rootRegion.isInitialized) rootRegion.theta = value
        }

    var speedCoefficient = 1.0
    var maxSpeedCoefficient = 1.0
    var toleranceCoefficient = 1.0
    var burnsHut = false
    var preventOverlapping = true
    var overlappingCoefficient = 100.0
    var edgeWeightCoefficient = 0.0

    private val vCenter = Point2D(
        vertices.fold(0.0) { acc, vView -> acc + vView.centerX / vertices.size },
        vertices.fold(0.0) { acc, vView -> acc + vView.centerY / vertices.size }
    )

    fun doIteration(): Map<VertexView, Point2D> {
        //Preparation of vertices
        for (vertexView in vertices) vertexView.vertex.layoutData.prepareToIteration()
        if (burnsHut) rootRegion = BurnsHutRegion(vertices, burnsHutTheta)
        //Repulsion
        if (burnsHut) {
            for (vertexView in vertices) repulsion.applyForce(rootRegion, vertexView, this::repulsionAdditionalCoefficient)
        } else {
            for (i in vertices.indices) {
                val vertexView1 = vertices[i]
                for (j in i + 1 until vertices.size) {
                    val vertexView2 = vertices[j]
                    repulsion.applyForce(vertexView1, vertexView2, this::repulsionAdditionalCoefficient)
                    repulsion.applyForce(vertexView2, vertexView1, this::repulsionAdditionalCoefficient)
                }
            }
        }
        //Attraction
        for (edgeView in edges) {
            attraction.applyForce(edgeView.vertexView1, edgeView.vertexView2) { v1, v2 ->
                attractionAdditionalCoefficient(v1, v2) * (edgeView.edge.weight).pow(edgeWeightCoefficient)
            }
            attraction.applyForce(edgeView.vertexView2, edgeView.vertexView1) { v1, v2 ->
                attractionAdditionalCoefficient(v1, v2) * (edgeView.edge.weight).pow(edgeWeightCoefficient)
            }
        }
        //Gravity
        for (vertexView in vertices) {
            gravity.applyForce(vCenter, vertexView)
        }
        //Global speed computation
        val traction = vertices.fold(0.0) { acc, v ->
            acc + (v.vertex.degree + 1) * v.traction
        }
        val swing = vertices.fold(0.0) { acc, v ->
            acc + (v.vertex.degree + 1) * v.swing
        }
        val globalSpeed =
            toleranceCoefficient * traction / swing //TODO: проверить, будет ли эффект, если ограничить как у авторов
        //Displacement of vertices computation
        for (vertexView in vertices) {
            val speed = minOf(
                speedCoefficient * globalSpeed / (1 + globalSpeed * swing.pow(0.5)),
                maxSpeedCoefficient / vertexView.vertex.layoutData.appliedForce.magnitude()
            )

            vertexView.vertex.layoutData.speed = speed
        }

        return vertices.associateWith {
            Point2D(
                it.vertex.layoutData.appliedForce.x * it.vertex.layoutData.speed,
                it.vertex.layoutData.appliedForce.y * it.vertex.layoutData.speed
            )
        }
    }

    private fun checkOverlapping(v1: VertexView, v2: VertexView): Boolean {
        return Point2D(v1.centerX, v1.centerY).distance(v2.centerX, v2.centerY) < v1.radius + v2.radius
    }

    private fun repulsionAdditionalCoefficient(v1: VertexView, v2: VertexView): Double {
        return if(preventOverlapping && checkOverlapping(v1, v2)) overlappingCoefficient
        else 1.0
    }

    private fun attractionAdditionalCoefficient(v1: VertexView, v2: VertexView): Double {
        return if(preventOverlapping && checkOverlapping(v1, v2)) 0.0
        else 1.0
    }
}