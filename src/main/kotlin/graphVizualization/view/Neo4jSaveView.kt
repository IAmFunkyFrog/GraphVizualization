package graphVizualization.view

import javafx.scene.Parent
import tornadofx.*

class Neo4jSaveView: Fragment() {
    val graphView = find(GraphView::class)

    override val root: Parent = vbox {
        textfield {
            textProperty().bindBidirectional(graphView.name)
        }
        button("Save") {
            action {
                //TODO доделать сохранение нормально
                println("Saving in Neo4j")
            }
        }
    }
}