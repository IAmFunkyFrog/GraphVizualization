import javafx.beans.property.DoubleProperty
import javafx.geometry.Point2D
import javafx.scene.paint.Color
import kotlin.math.pow

class LayoutData {
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

class Vertex<V>(
    var value: V
) {
    var layoutData: LayoutData = LayoutData()

    var degree: Int = 0
        private set

    fun onEdgeCreated() {
        degree++
    }

    fun onEdgeDeleted() {
        degree--
    }
}