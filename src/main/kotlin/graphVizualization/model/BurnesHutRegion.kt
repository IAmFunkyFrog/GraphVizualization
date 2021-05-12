package graphVizualization.model

import graphVizualization.view.VertexView
import javafx.geometry.Point2D

class BurnsHutRegion(
    val vertices: List<VertexView>,
    theta: Double
) {
    val subRegions: ArrayList<BurnsHutRegion> = ArrayList()

    var theta: Double = theta
        set(value) {
            field = value
            for (region in subRegions) region.theta = theta
        }
    val mass = vertices.fold(0.0) { acc, vView ->
        acc + vView.vertex.degree + 1
    }
    val massCenter = Point2D(
        vertices.fold(0.0) { acc, vView ->
            acc + vView.centerX * (vView.vertex.degree + 1)
        } / mass,
        vertices.fold(0.0) { acc, vView ->
            acc + vView.centerY * (vView.vertex.degree + 1)
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

        val lefts = vertices.filter {
            it.centerX < massCenter.x
        }
        val rights = vertices.filter {
            it.centerX >= massCenter.x
        }

        makeSubRegion(lefts.filter { it.centerY < massCenter.y })
        makeSubRegion(lefts.filter { it.centerY >= massCenter.y })
        makeSubRegion(rights.filter { it.centerY < massCenter.y })
        makeSubRegion(rights.filter { it.centerY >= massCenter.y })
    }

    private fun makeSubRegion(subVertices: List<VertexView>) {
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