package graphApp.controller

import Vertex
import graphApp.view.GraphView
import graphApp.view.VertexView
import javafx.geometry.Point2D
import java.util.concurrent.locks.ReentrantLock
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

fun getConnectionVector(
    v1: VertexView<*>,
    v2: VertexView<*>,
    force: (VertexView<*>, VertexView<*>) -> Double = { _, _ -> 1.0 }
): Point2D {
    val forceValue = force(v1, v2)
    return Point2D(
        v2.centerX - v1.centerX,
        v2.centerY - v1.centerY
    ).normalize().multiply(forceValue)
}

fun getDistanceBetween(v1: VertexView<*>, v2: VertexView<*>): Double {
    val v1Position = Point2D(v1.centerX, v1.centerY)
    val v2Position = Point2D(v2.centerX, v2.centerY)
    return v1Position.distance(v2Position)
}

class BurnsHutRegion(
    private val vertices: List<VertexView<*>>,
    private val theta: Double
) {
    private val innerRegions: ArrayList<BurnsHutRegion> = ArrayList()

    private val mass = vertices.fold(0.0) { acc, vView ->
        acc + vView.vertex.degree
    }
    private val massCenter = Point2D(
        vertices.fold(0.0) { acc, vView ->
            acc + vView.centerX * vView.vertex.degree / mass
        },
        vertices.fold(0.0) { acc, vView ->
            acc + vView.centerY * vView.vertex.degree / mass
        }
    )
    private val cellSize = vertices.fold(Double.MIN_VALUE) { m, vView ->
        maxOf(m, 2 * massCenter.distance(vView.centerX, vView.centerY))
    }

    init {
        makeInnerRegions()
    }

    fun applyForce(vertexView: VertexView<*>, repulsionStrategy: (VertexView<*>, VertexView<*>) -> Double) {
        if (vertices.size == 1) {
            val forceVector = getConnectionVector(vertices[0], vertexView, repulsionStrategy)
            vertexView.vertex.layoutData.applyForce(forceVector)
        } else {
            val distance = massCenter.distance(vertexView.centerX, vertexView.centerY)
            //TODO поправить кастыль
            if (cellSize <= distance * theta) {
                val forceValue =
                    0.1 * (vertexView.vertex.degree.toDouble() + 1) * (mass + 1) / distance
                val forceVector = Point2D(
                    vertexView.centerX - massCenter.x,
                    vertexView.centerY - massCenter.y,
                ).normalize().multiply(forceValue)
                vertexView.vertex.layoutData.applyForce(forceVector)
            } else {
                for (region in innerRegions) region.applyForce(vertexView, repulsionStrategy)
            }
        }
    }

    private fun makeInnerRegions() {
        if (vertices.size <= 1) return

        val upLefts = vertices.filter {
            it.centerX < massCenter.x && it.centerY < massCenter.y
        }
        val upRights = vertices.filter {
            it.centerX >= massCenter.x && it.centerY < massCenter.y
        }
        val downLefts = vertices.filter {
            it.centerX < massCenter.x && it.centerY >= massCenter.y
        }
        val downRights = vertices.filter {
            it.centerX >= massCenter.x && it.centerY >= massCenter.y
        }

        makeInnerRegion(upLefts)
        makeInnerRegion(upRights)
        makeInnerRegion(downLefts)
        makeInnerRegion(downRights)
    }

    private fun makeInnerRegion(innerVertices: List<VertexView<*>>) {
        if (innerVertices.isNotEmpty()) {
            if (innerVertices.size < vertices.size) innerRegions.add(BurnsHutRegion(innerVertices, theta))
            else {
                for (vertex in innerVertices) {
                    val oneVertexList = listOf(vertex)
                    innerRegions.add(BurnsHutRegion(oneVertexList, theta))
                }
            }
        }
    }
}

class ForceAtlas2(
    private val graph: GraphView<*>,
) {
    var repulsionCoefficient = 20.0
    var gravityCoefficient = 0.0001
    var speedCoefficient = 1
    var maxSpeedCoefficient = 1
    var toleranceCoefficient = 1
    var burnsHut: Boolean = true
    var burnsHutTheta = 1.2

    private var vertices = graph.vertices.values.toList()
    private var edges = graph.edges.values.toList()

    private var attractionStrategy: (VertexView<*>, VertexView<*>) -> Double = ::defaultAttractionStrategy
    private var repulsionStrategy: (VertexView<*>, VertexView<*>) -> Double = ::defaultRepulsionStrategy
    private var gravityStrategy: (VertexView<*>) -> Double = ::defaultGravityStrategy

    private val vCenter = Point2D(
        vertices.fold(0.0) { acc, vView -> acc + vView.centerX / vertices.size },
        vertices.fold(0.0) { acc, vView -> acc + vView.centerY / vertices.size }
    )
    private val rootRegion: BurnsHutRegion = BurnsHutRegion(vertices, burnsHutTheta)

    fun doIteration() {
        //Preparation of vertices
        for (vertexView in vertices) vertexView.vertex.layoutData.prepareToIteration()
        //Repulsion
        if (burnsHut) {
            for (vertexView in vertices) rootRegion.applyForce(vertexView, repulsionStrategy)
        } else {
            for (i in vertices.indices) {
                val vertexView1 = vertices[i]
                for (j in i + 1 until vertices.size) {
                    val vertexView2 = vertices[j]
                    val forceVector = getConnectionVector(vertexView1, vertexView2, repulsionStrategy)
                    vertexView1.vertex.layoutData.applyForce(forceVector.multiply(-1.0))
                    vertexView2.vertex.layoutData.applyForce(forceVector)
                }
            }
        }
        //Attraction
        for (edgeView in edges) {
            val forceVector = getConnectionVector(edgeView.vertexView1, edgeView.vertexView2, attractionStrategy)
            edgeView.vertexView1.vertex.layoutData.applyForce(forceVector)
            edgeView.vertexView2.vertex.layoutData.applyForce(forceVector.multiply(-1.0))
        }
        //Gravity
        //TODO поправить костыль
        for (vertexView in vertices) {
            val forceVector = Point2D(vCenter.x - vertexView.centerX, vCenter.y - vertexView.centerY)
                .multiply(gravityStrategy(vertexView))
            vertexView.vertex.layoutData.applyForce(forceVector)
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

    private fun defaultAttractionStrategy(v1: VertexView<*>, v2: VertexView<*>): Double {
        return getDistanceBetween(v1, v2)
    }

    private fun defaultRepulsionStrategy(v1: VertexView<*>, v2: VertexView<*>): Double {
        val distanceBetween = maxOf(getDistanceBetween(v1, v2), 0.001)
        return repulsionCoefficient * (v1.vertex.degree.toDouble() + 1) * (v2.vertex.degree.toDouble() + 1) / distanceBetween
    }

    private fun defaultGravityStrategy(v: VertexView<*>): Double {
        return gravityCoefficient * (v.vertex.degree + 1).toDouble()
    }
}