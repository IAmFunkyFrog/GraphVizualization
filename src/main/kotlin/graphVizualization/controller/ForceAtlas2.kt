package graphVizualization.controller

import graphVizualization.view.GraphView
import graphVizualization.view.VertexView
import javafx.geometry.Point2D
import kotlin.math.pow

//TODO: реализовать режим предотвращения коллизий
//TODO: Реализовать оптимизацию Burns-Hut
//TODO: Реализовать режим LinLog
//TODO: Реализовать поддержку учета взвешенных ребер
//TODO: Реализовать поддержку DissuadeHubs

val VertexView<*>.traction
    get() = vertex.layoutData.appliedForce.add(vertex.layoutData.oldAppliedForce).multiply(0.5).magnitude()

val VertexView<*>.swing
    get() = vertex.layoutData.appliedForce.add(vertex.layoutData.oldAppliedForce.multiply(-1.0)).multiply(0.5)
        .magnitude()

class ForceAtlas2<V>(
    private val graph: GraphView<V>,
) {
    private var vertices = graph.vertices.values.toList()
    private var edges = graph.edges.values.toList()

    private var attraction = Force.Factory.DistanceAttraction()
    private var repulsion = Force.Factory.DistanceRepulsion()
    private var gravity = Force.Factory.DefaultGravity()

    private val rootRegion: BurnsHutRegion<V> = BurnsHutRegion(vertices)

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

    var burnsHutTheta
        get() = rootRegion.theta
        set(value) {
            rootRegion.theta = value
        }

    var speedCoefficient = 1
    var maxSpeedCoefficient = 1
    var toleranceCoefficient = 1
    var burnsHut: Boolean = true

    private val vCenter = Point2D(
        vertices.fold(0.0) { acc, vView -> acc + vView.centerX / vertices.size },
        vertices.fold(0.0) { acc, vView -> acc + vView.centerY / vertices.size }
    )

    fun doIteration() {
        //Preparation of vertices
        for (vertexView in vertices) vertexView.vertex.layoutData.prepareToIteration()
        //Repulsion
        if (burnsHut) {
            for (vertexView in vertices) repulsion.applyForce(rootRegion, vertexView)
        } else {
            for (i in vertices.indices) {
                val vertexView1 = vertices[i]
                for (j in i + 1 until vertices.size) {
                    val vertexView2 = vertices[j]
                    repulsion.applyForce(vertexView1, vertexView2)
                }
            }
        }
        //Attraction
        for (edgeView in edges) {
            attraction.applyForce(edgeView.vertexView1, edgeView.vertexView2)
            attraction.applyForce(edgeView.vertexView2, edgeView.vertexView1)
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
        //Displacement of vertices
        for (vertexView in vertices) {
            val speed = minOf(
                speedCoefficient * globalSpeed / (1 + globalSpeed * swing.pow(0.5)),
                maxSpeedCoefficient / vertexView.vertex.layoutData.appliedForce.magnitude()
            )

            vertexView.centerX += vertexView.vertex.layoutData.appliedForce.x * speed
            vertexView.centerY += vertexView.vertex.layoutData.appliedForce.y * speed
        }
    }
}