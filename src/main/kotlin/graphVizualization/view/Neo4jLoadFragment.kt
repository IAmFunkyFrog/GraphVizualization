package graphVizualization.view

import graphVizualization.controller.Neo4jSaveLoadController
import javafx.scene.Parent
import javafx.scene.control.ListView
import tornadofx.*

class Neo4jLoadFragment(
    graphView: GraphView
): Fragment() {
    private val controller = Neo4jSaveLoadController()
    private val graphsListView = ListView<String>()

    override val root: Parent = vbox {
        add(graphsListView).also {
            runAsync {
                controller.getAllGraphs()
            } ui {
                for(graph in it) {
                    graphsListView.items.add(graph)
                }
            }
        }
        button("Select") {

        }
    }
}