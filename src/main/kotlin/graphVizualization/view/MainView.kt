package graphVizualization.view

import graphVizualization.styles.TopBarStyle
import javafx.scene.Parent
import javafx.scene.control.*
import tornadofx.*

class MainView() : View() {
    private val graphView = GraphView()
    private val algorithms = mapOf(
        "Distance" to graphView.controller::setDistanceAttraction,
        "LinLog" to graphView.controller::setLinLogAttraction
    )

    private val forceAtlas2Inputs = listOf(
        Pair("Attraction algorithm", ComboBox<String>().apply {
            for(name in algorithms.keys) items.add(name)
            selectionModel.selectedItemProperty().addListener { _, _, newValue ->
                algorithms[newValue]?.let { it() }
            }
            value = "Choose an algorithm"
        }),
        Pair("BurnsHut mode", CheckBox().apply {
            selectedProperty().addListener { _, _, newValue ->
                graphView.controller.forceAtlas2.burnsHut = newValue
            }
            isSelected = graphView.controller.forceAtlas2.burnsHut
        }),
        Pair("Prevent overlapping mode", CheckBox().apply {
            selectedProperty().addListener { _, _, newValue ->
                graphView.controller.forceAtlas2.preventOverlapping = newValue
            }
            isSelected = graphView.controller.forceAtlas2.preventOverlapping
        }),
        Pair("Dissuade hubs mode", CheckBox().apply {
            selectedProperty().addListener { _, _, newValue ->
                graphView.controller.forceAtlas2.dissuadeHubs = newValue
            }
            isSelected = graphView.controller.forceAtlas2.dissuadeHubs
        }),
        Pair("burnsHutTheta", NumberField(graphView.controller.forceAtlas2.burnsHutTheta) {
            graphView.controller.forceAtlas2.burnsHutTheta = it
        }),
        Pair("repulsion", NumberField(graphView.controller.forceAtlas2.repulsionCoefficient) {
            graphView.controller.forceAtlas2.repulsionCoefficient = it
        }),
        Pair("gravity", NumberField(graphView.controller.forceAtlas2.gravityCoefficient) {
            graphView.controller.forceAtlas2.gravityCoefficient = it
        }),
        Pair("tolerance", NumberField(graphView.controller.forceAtlas2.gravityCoefficient) {
            //TODO подумать над кастылем
            if(it != 0.0) graphView.controller.forceAtlas2.toleranceCoefficient = it
        })
    )

    override val root: Parent = borderpane {
        titleProperty.bind(graphView.name)

        center {
            add(graphView.root)

            this.setOnScroll {
                graphView.controller.onScroll(it, graphView)
            }
            this.setOnMousePressed {
                graphView.controller.onMousePressed(it, graphView)
            }
            this.setOnMouseDragged {
                graphView.controller.onMouseDragged(it, graphView)
            }
        }
        top = menubar {
            addClass(TopBarStyle.default)

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
        right = scrollpane {
            vbox {
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
                for ((desc, input) in forceAtlas2Inputs) {
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
}