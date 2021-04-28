package graphVizualization.view

import graphVizualization.controller.ForceAtlas2
import graphVizualization.controller.GraphController
import javafx.beans.value.ChangeListener
import javafx.scene.Parent
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.util.converter.NumberStringConverter
import tornadofx.View
import tornadofx.vbox

class ForceAtlas2ParamsView<V>(
    private var graphController: GraphController<*>,
    private var forceAtlas2: ForceAtlas2
): View() {

    private val repulsionCoefficientField = createNumberField()


    override val root: Parent = vbox {

    }

    fun resetForceAtlas2(forceAtlas2: ForceAtlas2) {
        this.forceAtlas2 = forceAtlas2


    }

    private fun createNumberField(): TextField {
        val newField = TextField()
        newField.textFormatter = TextFormatter(NumberStringConverter())
        newField.textProperty().addListener(ChangeListener {
            _, _, newValue -> forceAtlas2.repulsionCoefficient = newValue.toDouble()
        })
        return newField
    }
}