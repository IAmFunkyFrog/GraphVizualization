package graphApp.view

import graphApp.model.Graph
import javafx.scene.Parent
import tornadofx.*

class MainView() : View() {
    private val graph = GraphView(Graph.RandomGraph())

    override val root: Parent = borderpane {
        center {
            add(graph)
        }
        left = vbox {
            button("Reset ForceAtlas2") {
                action {
                    graph.controller.resetForceAtlas2()
                }
            }
            button("Tap") {
                action {
                    graph.controller.doForceAtlas2()
                }
            }
        }
    }
}