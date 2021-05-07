import graphVizualization.view.MainView
import javafx.stage.Stage
import tornadofx.App
import tornadofx.launch

class MainApp: App(MainView::class) {
    override fun start(stage: Stage) {
        with(stage) {
            width = 640.0
            height = 640.0
        }
        super.start(stage)
    }
}

fun main() {
    launch<MainApp>()
}