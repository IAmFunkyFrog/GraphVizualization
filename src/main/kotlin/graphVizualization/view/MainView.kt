package graphVizualization.view

import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.TextField
import tornadofx.*

class MainView() : View() {
    private val graphView = GraphView()

    private val forceAtlas2Inputs = listOf(
        Pair("repulsionCoefficient", NumberField(graphView.controller.forceAtlas2.repulsionCoefficient) {
            graphView.controller.forceAtlas2.repulsionCoefficient = it
        }),
        Pair("gravityCoefficient", NumberField(graphView.controller.forceAtlas2.gravityCoefficient) {
            graphView.controller.forceAtlas2.gravityCoefficient = it
        }),
        Pair("burnsHutTheta", NumberField(graphView.controller.forceAtlas2.burnsHutTheta) {
            graphView.controller.forceAtlas2.burnsHutTheta = it
        }),
        Pair("speedCoefficient", NumberField(graphView.controller.forceAtlas2.speedCoefficient) {
            graphView.controller.forceAtlas2.speedCoefficient = it
        }),
        Pair("maxSpeedCoefficient", NumberField(graphView.controller.forceAtlas2.maxSpeedCoefficient) {
            graphView.controller.forceAtlas2.maxSpeedCoefficient = it
        }),
        Pair("toleranceCoefficient", NumberField(graphView.controller.forceAtlas2.toleranceCoefficient) {
            graphView.controller.forceAtlas2.toleranceCoefficient = it
        })
    )

    override val root: Parent = borderpane {
        top = hbox {
            menubar {
                Menu("Save").also {
                    this.menus.add(it)

                    it.items.add(MenuItem("in Neo4j").apply {
                        setOnAction {
                            openInternalWindow(Neo4jSaveFragment(graphView.graph, graphView.name))
                        }
                    })
                }
                Menu("Load").also {
                    this.menus.add(it)

                    it.items.add(MenuItem("from Neo4j").apply {
                        setOnAction {
                            openInternalWindow(Neo4jLoadFragment(graphView))
                        }
                    })
                }
            }
        }
        center = graphView.root
        left = vbox {
            button("Start") {
                action {
                    graphView.controller.startForceAtlas2()
                }
            }
            button("Cancel") {
                action {
                    graphView.controller.cancelForceAtlas2()
                }
            }
            for((desc, input) in forceAtlas2Inputs) {
                label(desc) {
                    labelFor = input
                }
                add(input)
            }
            form {
                Label("Create vertex").also {
                    add(it)
                    it.labelFor = this
                }
                TextField().also {
                    add(it)
                    button("Create") {
                        action {
                            graphView.controller.createVertex(it.text)
                        }
                    }
                }
            }
        }
    }
}