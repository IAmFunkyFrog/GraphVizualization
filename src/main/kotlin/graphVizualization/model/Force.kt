package graphVizualization.model

import graphVizualization.view.VertexView
import javafx.geometry.Point2D
import kotlin.math.ln
import kotlin.math.nextUp

typealias ForceFunction = (Point2D, Point2D, Double, Double) -> Point2D

abstract class Force {
    var coefficient = 1.0

    abstract val forceFunction: ForceFunction

    fun applyForce(vertexFrom: VertexView, vertexTo: VertexView, additionalCoefficient: ((VertexView, VertexView) -> Double)? = null) {
        var forceVector = forceFunction(
            Point2D(vertexFrom.centerX, vertexFrom.centerY),
            Point2D(vertexTo.centerX, vertexTo.centerY),
            vertexFrom.vertex.degree.toDouble(),
            vertexTo.vertex.degree.toDouble(),
        )

        if(additionalCoefficient != null) forceVector = forceVector.multiply(additionalCoefficient(vertexFrom, vertexTo))

        vertexTo.vertex.layoutData.applyForce(forceVector)
    }

    fun applyForce(region: BurnsHutRegion, vertexTo: VertexView, additionalCoefficient: ((VertexView, VertexView) -> Double)? = null) {
        if (region.vertices.size == 1) {
            applyForce(region.vertices[0], vertexTo, additionalCoefficient)
        } else {
            val distance = region.massCenter.distance(vertexTo.centerX, vertexTo.centerY)
            if (region.cellSize <= distance * region.theta) {
                val forceVector = forceFunction(
                    region.massCenter,
                    Point2D(vertexTo.centerX, vertexTo.centerY),
                    region.mass,
                    vertexTo.vertex.degree.toDouble(),
                )
                vertexTo.vertex.layoutData.applyForce(forceVector)
            } else {
                for (subRegion in region.subRegions) applyForce(subRegion, vertexTo)
            }
        }
    }

    fun applyForce(pointFrom: Point2D, vertexTo: VertexView, additionalCoefficient: ((Point2D, VertexView) -> Double)? = null) {
        var forceVector = forceFunction(
            pointFrom,
            Point2D(vertexTo.centerX, vertexTo.centerY),
            1.0, //MUST NOT BE USED IN CALCULATIONS
            vertexTo.vertex.degree.toDouble(),
        )

        if(additionalCoefficient != null) forceVector = forceVector.multiply(additionalCoefficient(pointFrom, vertexTo))

        vertexTo.vertex.layoutData.applyForce(forceVector)
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

        class DissuadeHubsAttraction: Force() {
            override val forceFunction: ForceFunction = { pointFrom, pointTo, massFrom, _ ->
                val forceValue = pointFrom.distance(pointTo) / (massFrom + 1)
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

        class NoGravity: Force() {
            override val forceFunction: ForceFunction = { pointFrom, pointTo, _, massTo ->
                val forceValue = 0.0
                Point2D(
                    pointFrom.x - pointTo.x,
                    pointFrom.y - pointTo.y
                ).normalize().multiply(forceValue)
            }
        }
    }
}