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
import java.util.*
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

data class NameColor(val name: String, val color: Int)
data class NameValue(val name: String, val value: Int)

fun main() {
    createWindow(
        "pf-2021-viz",
        Diagram.PIE,
        listOf(
            NameValue("aa", 400),
            NameValue("a", 20),
            NameValue("a", 20),
            NameValue("a", 20)
        )
    )
}

fun createWindow(title: String, type: Diagram, listNameValue: List<NameValue>) = runBlocking(Dispatchers.Swing) {
    val window = SkiaWindow()
    window.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
    window.title = title

    window.layer.renderer = Renderer(window.layer, type, listNameValue)
    window.layer.addMouseMotionListener(MyMouseMotionAdapter)

    window.preferredSize = Dimension(900, 600)
    window.minimumSize = Dimension(100, 100)
    window.pack()
    window.layer.awaitRedraw()
    window.isVisible = true
}

class Renderer(val layer: SkiaLayer, val type: Diagram, val listNameValue: List<NameValue>) : SkiaRenderer {
    val typeface = Typeface.makeFromFile("fonts/JetBrainsMono-Regular.ttf")
    val font = Font(typeface, 40f)
    val paint = Paint().apply {
        color = 0xff9BC730L.toInt()
        mode = PaintMode.FILL
        strokeWidth = 1f
    }
    val randomColor = List(listNameValue.size) {
        Color.makeARGB(
            255,
            Random().nextInt(256),
            Random().nextInt(256),
            Random().nextInt(256)
        )
    }
    val listNameColor = List(listNameValue.size) { NameColor(listNameValue[it].name, randomColor[it]) }

    override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
        val contentScale = layer.contentScale
        canvas.scale(contentScale, contentScale)
        val w = (width / contentScale).toInt()
        val h = (height / contentScale).toInt()
        val k = 0.9
        val centerX = w / 3
        val centerY = h / 2
        val radius = Math.min(h / 2 * k, w / 3 * k).toInt()

        val totalValue = listNameValue.sumOf { it.value }
        fun getStepSize(value: Double, total: Int): Double {
            return (360 * value) / total
        }

        var start = 0.0
        val steps = listNameValue.size
        var stepSize: Double
        for (i in 0..steps - 1) {
            stepSize = getStepSize(listNameValue[i].value.toDouble(), totalValue)
            paint.color = listNameColor[i].color
            canvas.drawArc(
                (centerX - radius).toFloat(),
                (centerY - radius).toFloat(),
                (centerX + radius).toFloat(),
                (centerY + radius).toFloat(),
                start.toFloat(),
                stepSize.toFloat(),
                true,
                paint
            )
            start += stepSize
        }

        val upLeftX = w / 2 + w * k / 6
        val upLeftY = h * (1 - k) / 2
        val d = h * k / steps
        font.setSize((2 * d / 3).toFloat())
        for (i in 0..steps - 1) {
            paint.color = listNameColor[i].color
            canvas.drawString(
                listNameColor[i].name,
                upLeftX.toFloat(),
                (i * d + upLeftY).toFloat() + font.size,
                font,
                paint
            )
        }

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