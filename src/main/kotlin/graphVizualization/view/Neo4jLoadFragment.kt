package graphVizualization.view

import graphVizualization.controller.Neo4jSaveLoadController
import javafx.beans.property.SimpleStringProperty
import javafx.scene.Parent
import javafx.scene.control.ListView
import tornadofx.*

class Neo4jLoadFragment(
    graphView: GraphView
) : Fragment() {
    private val controller = Neo4jSaveLoadController()
    private val graphsListView = ListView<String>()

    private val status = SimpleStringProperty("Graph not selected")

    override val root: Parent = vbox {
        add(graphsListView).also {
            runAsync {
                controller.getAllGraphNames()
            } ui {
                for (graph in it) {
                    graphsListView.items.add(graph)
                }
                graphsListView
            }
        }
        label {
            id = "status_lbl"
            textProperty().bind(status)
        }
        button("Select") {
            id = "select_btn"
            action {
                graphsListView.selectionModel.selectedItem?.let { name ->
                    status.value = "Loading"
                    runAsync {
                        controller.getGraphByName(name)
                    } ui { graph ->
                        graphView.resetGraph(graph, name)
                        status.value = "Loaded"
                    }
                }
            }
        }
    }
}