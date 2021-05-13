package graphVizualization.controller

import graphVizualization.model.SQLiteSaveLoadModel
import graphVizualization.view.GraphView
import javafx.stage.FileChooser
import tornadofx.*
import java.lang.RuntimeException

class SQLiteSaveLoadController(private val graphView: GraphView) : Controller() {
    fun saveGraph() {
        val files = chooseFile(
            title = "Save in SQLite",
            filters = arrayOf(FileChooser.ExtensionFilter("SQLite", "*.sqlite", "*.sqlite3")),
            mode = FileChooserMode.Save,
        )
        if (files.isEmpty()) return
        val file = files[0]
        runAsync {
            SQLiteSaveLoadModel.save(graphView.graph, file)
        } fail {
            throw RuntimeException("Cannot save graph to $file")
        } // ui

    }

    fun loadGraph() {
        val files = chooseFile(
            title = "Load from SQLite",
            filters = arrayOf(FileChooser.ExtensionFilter("SQLite", "*.sqlite3", "*.sqlite")),
            mode = FileChooserMode.Single,
        )
        if (files.isEmpty()) return
        val file = files[0]
        runAsync {
            SQLiteSaveLoadModel.load(file)
        } success {
            graphView.resetGraph(it, file.name)
        } fail {
            throw RuntimeException("Cannot load graph from $file")
        }
    }
}