package graphVizualization.controller

import graphVizualization.view.VertexView
import javafx.geometry.Point2D
import kotlin.math.nextDown

class BurnsHutRegion<V>(
    val vertices: List<VertexView<V>>
) {
    val subRegions: ArrayList<BurnsHutRegion<V>> = ArrayList()

    var theta: Double = 1.0
        set(value) {
            field = value
            for (region in subRegions) region.theta = theta
        }

    val mass = vertices.fold(0.0) { acc, vView ->
        acc + vView.vertex.degree
    }.nextDown()
    val massCenter = Point2D(
        vertices.fold(0.0) { acc, vView ->
            acc + vView.centerX * vView.vertex.degree
        } / mass,
        vertices.fold(0.0) { acc, vView ->
            acc + vView.centerY * vView.vertex.degree
        } / mass
    )
    val cellSize = vertices.fold(Double.MIN_VALUE) { m, vView ->
        maxOf(m, 2 * massCenter.distance(vView.centerX, vView.centerY))
    }

    init {
        makeSubRegions()
    }

    private fun makeSubRegions() {
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

        makeSubRegion(upLefts)
        makeSubRegion(upRights)
        makeSubRegion(downLefts)
        makeSubRegion(downRights)
    }

    private fun makeSubRegion(subVertices: List<VertexView<V>>) {
        if (subVertices.isNotEmpty()) {
            if (subVertices.size < vertices.size) subRegions.add(BurnsHutRegion<V>(subVertices))
            else {
                for (vertex in subVertices) {
                    val oneVertexList = listOf(vertex)
                    subRegions.add(BurnsHutRegion<V>(oneVertexList))
                }
            }
        }
    }
}