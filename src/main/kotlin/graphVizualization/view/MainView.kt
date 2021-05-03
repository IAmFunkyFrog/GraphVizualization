package graphVizualization.view

import graphVizualization.model.Graph
import javafx.scene.Parent
import javafx.scene.control.TextField
import javafx.scene.layout.Pane
import tornadofx.*

class MainView() : View() {
    private val graph = GraphView(Graph.RandomGraph())

    private val forceAtlas2Inputs = listOf(
        Pair("repulsionCoefficient", createNumberField(graph.controller.forceAtlas2.repulsionCoefficient) {
            graph.controller.forceAtlas2.repulsionCoefficient = it
        }),
        Pair("gravityCoefficient", createNumberField(graph.controller.forceAtlas2.gravityCoefficient) {
            graph.controller.forceAtlas2.gravityCoefficient = it
        }),
        Pair("burnsHutTheta", createNumberField(graph.controller.forceAtlas2.burnsHutTheta) {
            graph.controller.forceAtlas2.burnsHutTheta = it
        }),
        Pair("speedCoefficient", createNumberField(graph.controller.forceAtlas2.speedCoefficient) {
            graph.controller.forceAtlas2.speedCoefficient = it
        }),
        Pair("maxSpeedCoefficient", createNumberField(graph.controller.forceAtlas2.maxSpeedCoefficient) {
            graph.controller.forceAtlas2.maxSpeedCoefficient = it
        }),
        Pair("toleranceCoefficient", createNumberField(graph.controller.forceAtlas2.toleranceCoefficient) {
            graph.controller.forceAtlas2.toleranceCoefficient = it
        })
    )

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
            button("Start") {
                action {
                    graph.controller.startForceAtlas2()
                }
            }
            button("Cancel") {
                action {
                    graph.controller.cancelForceAtlas2()
                }
            }
            for((desc, input) in forceAtlas2Inputs) {
                add(label(desc) {
                    labelFor = input
                })
                add(input)
            }
        }
    }

    private fun createNumberField(
        initValue: Double,
        onChange: (Double) -> Unit
    ): TextField {
        val newField = TextField(initValue.toString())
        newField.textProperty().addListener { _, _, newValue ->
            try {
                onChange(newValue.toDouble())
            }
            catch (e: Exception) {
                //TODO добавить нормальное оповещение
                println("parse error in forceAtlas2Params")
            }
        }
        return newField
    }
}