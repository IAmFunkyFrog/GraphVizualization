package graphVizualization.view

import graphVizualization.controller.Neo4jSaveLoadController
import graphVizualization.model.Graph
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.Parent
import tornadofx.*

class Neo4jSaveFragment(
    graph: Graph,
    name: StringProperty
): Fragment() {
    private val controller = Neo4jSaveLoadController()

    private val status = SimpleStringProperty("")

    override val root: Parent = vbox {
        textfield {
            textProperty().bindBidirectional(name)
        }
        label {
            id = "status_lbl"
            this.textProperty().bind(status)
        }
        button("Save") {
            id = "save_btn"
            action {
                //TODO подумать над добавлением логера
                status.value = "Saving"
                runAsync {
                    controller.saveGraph(graph, name.value)
                } ui {
                    status.value = "Graph saved"
                }
            }
        }
    }
}