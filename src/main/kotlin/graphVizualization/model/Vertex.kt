import javafx.beans.property.StringProperty
import javafx.geometry.Point2D
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.random.Random

class LayoutData {
    var radius = 10.0
    var delta = Point2D(Random.nextInt().absoluteValue.toDouble() % 100, Random.nextInt().absoluteValue.toDouble() % 100)
    var appliedForce: Point2D = Point2D(0.0, 0.0)
        private set
    var oldAppliedForce: Point2D = Point2D(0.0, 0.0)
        private set

    fun applyForce(force: Point2D) {
        appliedForce = appliedForce.add(force)
    }
    fun prepareToIteration() {
        oldAppliedForce = appliedForce
        appliedForce = Point2D(0.0, 0.0)
    }
}

class Vertex(
    var value: String
): Comparable<Vertex> {
    var layoutData: LayoutData = LayoutData()
    var centrality: Double = 0.0

    var degree: Int = 0
        private set

    fun onEdgeCreated() {
        degree++
    }

    fun onEdgeDeleted() {
        degree--
    }

    override fun compareTo(other: Vertex): Int {
        return value.compareTo(other.value)
    }
}