import graphVizualization.view.MainView
import javafx.stage.Stage
import tornadofx.App
import tornadofx.launch

class MainApp: App(MainView::class) {
    override fun start(stage: Stage) {
        stage.isMaximized = true
        super.start(stage)
    }
}

fun main() {
    launch<MainApp>()
}