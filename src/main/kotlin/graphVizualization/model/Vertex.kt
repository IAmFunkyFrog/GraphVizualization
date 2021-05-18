import javafx.beans.property.StringProperty
import javafx.geometry.Point2D
import javafx.scene.paint.Color
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.random.Random

class LayoutData {
    var radius = 10.0
    var fill = Color.RED
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

    var community: Int = 0
    set(value) {
        val rgbMax = 256
        val defaultRedValue = 255
        val defaultGreenValue = 0
        val defaultBlueValue = 0
        val red = ((defaultRedValue + 15 * value) % rgbMax + rgbMax) % rgbMax
        val green = ((defaultGreenValue + 31 * value) % rgbMax + rgbMax) % rgbMax
        val blue = ((defaultBlueValue + 63 * value) % rgbMax + rgbMax) % rgbMax
        layoutData.fill = Color.rgb(red, green, blue)
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

    override fun compareTo(other: Vertex): Int {
        return value.compareTo(other.value)
    }
}