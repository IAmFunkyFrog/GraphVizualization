package graphVizualization.model.forceAtlas2

import Vertex
import graphVizualization.view.VertexView
import javafx.geometry.Point2D

class BurnsHutRegion(
    val vertices: List<Vertex>,
    theta: Double
) {
    val subRegions: ArrayList<BurnsHutRegion> = ArrayList()

    var theta: Double = theta
        set(value) {
            field = value
            for (region in subRegions) region.theta = theta
        }
    val mass = vertices.fold(0.0) { acc, v ->
        acc + v.degree + 1
    }
    val massCenter = Point2D(
        vertices.fold(0.0) { acc, v ->
            acc + v.layoutData.delta.x * (v.degree + 1)
        } / mass,
        vertices.fold(0.0) { acc, v ->
            acc + v.layoutData.delta.y * (v.degree + 1)
        } / mass
    )
    val cellSize = vertices.fold(Double.MIN_VALUE) { m, v ->
        maxOf(m, 2 * massCenter.distance(v.layoutData.delta.x, v.layoutData.delta.y))
    }

    init {
        makeSubRegions()
    }

    private fun makeSubRegions() {
        if (vertices.size <= 1) return

        val lefts = vertices.filter {
            it.layoutData.delta.x < massCenter.x
        }
        val rights = vertices.filter {
            it.layoutData.delta.x >= massCenter.x
        }

        makeSubRegion(lefts.filter { it.layoutData.delta.y < massCenter.y })
        makeSubRegion(lefts.filter { it.layoutData.delta.y >= massCenter.y })
        makeSubRegion(rights.filter { it.layoutData.delta.y < massCenter.y })
        makeSubRegion(rights.filter { it.layoutData.delta.y >= massCenter.y })
    }

    private fun makeSubRegion(subVertices: List<Vertex>) {
        if (subVertices.isNotEmpty()) {
            if (subVertices.size < vertices.size) subRegions.add(BurnsHutRegion(subVertices, theta))
            else {
                for (vertex in subVertices) {
                    val oneVertexList = listOf(vertex)
                    subRegions.add(BurnsHutRegion(oneVertexList, theta))
                }
            }
        }
    }
}