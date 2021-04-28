import graphApp.view.MainView
import tornadofx.App
import tornadofx.launch

class MainApp: App(MainView::class) {

}

fun main() {
    launch<MainApp>()
}