package graphVizualization.view

import javafx.scene.control.TextField

class NumberField(
    initValue: Double,
    onChange: (Double) -> Unit
): TextField() {
    init {
        text = initValue.toString()
        textProperty().addListener { _, oldValue, newValue ->
            try {
                onChange(newValue.toDouble())
            }
            catch (e: Exception) {
                //TODO добавить нормальное оповещение или еще что нибудь
                println(e.message)
                text = oldValue
            }
        }
    }
}