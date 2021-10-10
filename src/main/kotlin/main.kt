import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.swing.Swing
import org.jetbrains.skija.*
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkiaRenderer
import org.jetbrains.skiko.SkiaWindow
import java.awt.Dimension
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.io.File
import javax.swing.WindowConstants

enum class Diagram(val diagram: String) {
    SCHEDULE("schedule_diagram"), PIE("pie_diagram"), COLUMN("column_diagram"), ERR("error");

    companion object {
        fun getDiagramFromString(str: String): Diagram {
            for (value in values()) {
                if (value.diagram == str) {
                    return value
                }
            }
            return ERR
        }
    }
}

enum class Error(val str: String) {
    NOT_ALL_ARGUMENTS("Неверный формат ввода данных. Ожидается: <dataFileName> <output.png> diagramType"),
    FILE_IS_NOT_EXIST("Файла с данным названием не существует."),
    DIAGRAM_TYPE_IS_NOT_EXIST("Данный тип диаграммы не поддерживается.");
}

class Input(args: Array<String>) {
    var dataFile: File? = null
    var outputFile: File? = null
    var diagramType: Diagram? = Diagram.ERR

    init {

        if (args.size > 0 && File(args[0]).isFile) {
            dataFile = File(args[0])
        }
        if (args.size > 1 && File(args[1]).isFile) {
            outputFile = File(args[1])
        }
        if (args.size > 3) {
            diagramType = Diagram.getDiagramFromString(args[2])
        }
    }

    fun check(): Error? {
        if (diagramType == Diagram.ERR) {
            return Error.DIAGRAM_TYPE_IS_NOT_EXIST
        }
        if (outputFile == null || dataFile == null) {
            return Error.FILE_IS_NOT_EXIST
        }
        return null
    }
}

fun main(args: Array<String>) {
    if (args.size == 3) {
        val input = Input(args)
        val errorInInput = input.check()
        if (errorInInput == null) {
            TODO()
        } else {
            printError(errorInInput)
        }
    } else {
        printError(Error.NOT_ALL_ARGUMENTS)
    }
    createWindow("pf-2021-viz")
}

fun printError(error: Error) {
    TODO()
}

fun createWindow(title: String) = runBlocking(Dispatchers.Swing) {
    val window = SkiaWindow()
    window.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
    window.title = title

    window.layer.renderer = Renderer(window.layer)
    window.layer.addMouseMotionListener(MyMouseMotionAdapter)

    window.preferredSize = Dimension(800, 600)
    window.minimumSize = Dimension(100, 100)
    window.pack()
    window.layer.awaitRedraw()
    window.isVisible = true
}

class Renderer(val layer: SkiaLayer) : SkiaRenderer {
    val typeface = Typeface.makeFromFile("fonts/JetBrainsMono-Regular.ttf")
    val font = Font(typeface, 40f)
    val paint = Paint().apply {
        color = 0xff9BC730L.toInt()
        mode = PaintMode.FILL
        strokeWidth = 1f
    }

    override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
        val contentScale = layer.contentScale
        canvas.scale(contentScale, contentScale)
        val w = (width / contentScale).toInt()
        val h = (height / contentScale).toInt()

        // РИСОВАНИЕ

        layer.needRedraw()
    }
}

object State {
    var mouseX = 0f
    var mouseY = 0f
}

object MyMouseMotionAdapter : MouseMotionAdapter() {
    override fun mouseMoved(event: MouseEvent) {
        State.mouseX = event.x.toFloat()
        State.mouseY = event.y.toFloat()
    }
}