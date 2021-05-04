package graphVizualization.view

import graphVizualization.model.Graph
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.TextField
import tornadofx.*

class MainView() : View() {
    private val graphView = GraphView(Graph.EmptyGraph())

    private val forceAtlas2Inputs = listOf(
        Pair("repulsionCoefficient", createNumberField(graphView.controller.forceAtlas2.repulsionCoefficient) {
            graphView.controller.forceAtlas2.repulsionCoefficient = it
        }),
        Pair("gravityCoefficient", createNumberField(graphView.controller.forceAtlas2.gravityCoefficient) {
            graphView.controller.forceAtlas2.gravityCoefficient = it
        }),
        Pair("burnsHutTheta", createNumberField(graphView.controller.forceAtlas2.burnsHutTheta) {
            graphView.controller.forceAtlas2.burnsHutTheta = it
        }),
        Pair("speedCoefficient", createNumberField(graphView.controller.forceAtlas2.speedCoefficient) {
            graphView.controller.forceAtlas2.speedCoefficient = it
        }),
        Pair("maxSpeedCoefficient", createNumberField(graphView.controller.forceAtlas2.maxSpeedCoefficient) {
            graphView.controller.forceAtlas2.maxSpeedCoefficient = it
        }),
        Pair("toleranceCoefficient", createNumberField(graphView.controller.forceAtlas2.toleranceCoefficient) {
            graphView.controller.forceAtlas2.toleranceCoefficient = it
        })
    )

    override val root: Parent = borderpane {
        center {
            add(graphView)
        }
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
                add(label(desc) {
                    labelFor = input
                })
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