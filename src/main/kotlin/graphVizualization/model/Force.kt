package graphVizualization.model

import Vertex
import javafx.geometry.Point2D
import kotlin.math.ln

typealias ForceFunction = (Point2D, Point2D, Double, Double) -> Point2D

abstract class Force {
    var coefficient = 1.0

    abstract val forceFunction: ForceFunction

    fun applyForce(vertexFrom: Vertex, vertexTo: Vertex, additionalCoefficient: ((Vertex, Vertex) -> Double)? = null) {
        var forceVector = forceFunction(
            Point2D(vertexFrom.layoutData.delta.x, vertexFrom.layoutData.delta.y),
            Point2D(vertexTo.layoutData.delta.x, vertexTo.layoutData.delta.y),
            vertexFrom.degree.toDouble(),
            vertexTo.degree.toDouble(),
        )

        if(additionalCoefficient != null) forceVector = forceVector.multiply(additionalCoefficient(vertexFrom, vertexTo))

        vertexTo.layoutData.applyForce(forceVector)
    }

    fun applyForce(region: BurnsHutRegion, vertexTo: Vertex, additionalCoefficient: ((Vertex, Vertex) -> Double)? = null) {
        if (region.vertices.size == 1 && region.vertices[0] != vertexTo) {
            applyForce(region.vertices[0], vertexTo, additionalCoefficient)
        } else {
            val distance = region.massCenter.distance(vertexTo.layoutData.delta.x, vertexTo.layoutData.delta.y)
            if (region.cellSize < distance * region.theta) {
                val forceVector = forceFunction(
                    region.massCenter,
                    Point2D(vertexTo.layoutData.delta.x, vertexTo.layoutData.delta.y),
                    region.mass,
                    vertexTo.degree.toDouble(),
                )
                vertexTo.layoutData.applyForce(forceVector)
            } else {
                for (subRegion in region.subRegions) applyForce(subRegion, vertexTo)
            }
        }
    }

    fun applyForce(pointFrom: Point2D, vertexTo: Vertex, additionalCoefficient: ((Point2D, Vertex) -> Double)? = null) {
        var forceVector = forceFunction(
            pointFrom,
            Point2D(vertexTo.layoutData.delta.x, vertexTo.layoutData.delta.y),
            1.0, //MUST NOT BE USED IN CALCULATIONS
            vertexTo.degree.toDouble(),
        )

        if(additionalCoefficient != null) forceVector = forceVector.multiply(additionalCoefficient(pointFrom, vertexTo))

        vertexTo.layoutData.applyForce(forceVector)
    }

    companion object Factory {
        class DistanceAttraction: Force() {
            override val forceFunction: ForceFunction = { pointFrom, pointTo, _, _ ->
                val forceValue = pointFrom.distance(pointTo)
                Point2D(
                    pointFrom.x - pointTo.x,
                    pointFrom.y - pointTo.y
                ).normalize().multiply(forceValue)
            }
        }

        class LinLogAttraction: Force() {
            override val forceFunction: ForceFunction = { pointFrom, pointTo, _, _ ->
                val forceValue = ln(1 + pointFrom.distance(pointTo))
                Point2D(
                    pointFrom.x - pointTo.x,
                    pointFrom.y - pointTo.y
                ).normalize().multiply(forceValue)
            }
        }

        class DistanceRepulsion: Force() {
            override val forceFunction: ForceFunction = { pointFrom, pointTo, massFrom, massTo ->
                val distance = maxOf(pointFrom.distance(pointTo), 0.1)
                val forceValue = this.coefficient * (massFrom + 1) * (massTo + 1) / distance
                Point2D(
                    pointTo.x - pointFrom.x,
                    pointTo.y - pointFrom.y
                ).normalize().multiply(forceValue)
            }
        }

        class DefaultGravity: Force() {
            override val forceFunction: ForceFunction = { pointFrom, pointTo, _, massTo ->
                val forceValue = this.coefficient * (massTo + 1)
                Point2D(
                    pointFrom.x - pointTo.x,
                    pointFrom.y - pointTo.y
                ).normalize().multiply(forceValue)
            }
        }
    }
}