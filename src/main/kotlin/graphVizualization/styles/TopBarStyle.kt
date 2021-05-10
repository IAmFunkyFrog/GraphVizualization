package graphVizualization.styles

import javafx.scene.paint.Color
import tornadofx.Stylesheet
import tornadofx.cssclass

class TopBarStyle: Stylesheet() {

    companion object {
        val default by cssclass()
    }

    init {
        default {
            backgroundColor += Color.GREY
        }
    }
}