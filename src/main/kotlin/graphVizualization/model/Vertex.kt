import javafx.beans.property.StringProperty
import javafx.geometry.Point2D
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.random.Random

class LayoutData {
    var radius = 10.0
    var delta = Point2D(Random.nextInt().absoluteValue.toDouble() % 300, Random.nextInt().absoluteValue.toDouble() % 300)
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
) {
    var layoutData: LayoutData = LayoutData()
    var centralityScale = 1.13
        set(value) {
            layoutData.radius = 10 + value.pow(centrality * 100)
            field = value
        }
    var centrality: Double = 0.0
        set(value) {
            layoutData.radius = 10 + centralityScale.pow(value * 100)
            field = value
        }

    var degree: Int = 0
        private set

    fun onEdgeCreated() {
        degree++
    }

    fun onEdgeDeleted() {
        degree--
    }
}