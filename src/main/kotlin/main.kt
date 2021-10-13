import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.swing.Swing
import org.jetbrains.skija.*
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkiaRenderer
import org.jetbrains.skiko.SkiaWindow
import java.awt.Dimension
import java.io.File
import java.util.Random
import javax.swing.WindowConstants
import kotlin.math.min

enum class Diagram(val diagram: String) {
    SCHEDULE("schedule_diagram"), PIE("pie_diagram"), COLUMN("column_diagram");

    companion object {
        fun getDiagramFromString(str: String): Diagram? {
            for (value in values()) {
                if (value.diagram == str) {
                    return value
                }
            }
            return null
        }
    }
}

data class NameColor(val name: String, val color: Int)
data class NameValue(val name: String, val value: Int)

class Input(args: Array<String>) {
    var type: Diagram
    var data: List<NameValue>
    var outputFileName: String

    init {
        if (args.size != 3) {
            throw(NotCorrectInputData())
        }

        val typeOrNull = Diagram.getDiagramFromString(args[0])
        if (typeOrNull == null) {
            throw(NotCorrectDiagramType(args[0]))
        } else {
            type = typeOrNull
        }

        if (!File(args[1]).isFile) {
            throw(FileIsNotExist(args[1]))
        } else {
            data = getListOfNameValue(File(args[1]))
        }

        outputFileName = args[1]
    }

    private fun getListOfNameValue(file: File): List<NameValue> {
        val list = mutableListOf<NameValue>()
        for (line in file.readLines()) {
            val name = line.substringAfter(' ')
            val value = line.substringBefore(' ').toIntOrNull()
            if(name==""||value==null){
                throw(NotCorrectDataInFile(line))
            }
            list.add(NameValue(name,value))
        }
        return list
    }
}

fun main(args: Array<String>) {
    try {
        val input = Input(args)
        createWindow(
            "pf-2021-viz",
            input.type,
            input.data
        )
    } catch (e: Exception){
        println(e.message)
    }
}

fun createWindow(title: String, type: Diagram, listNameValue: List<NameValue>) = runBlocking(Dispatchers.Swing) {
    val window = SkiaWindow()
    window.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
    window.title = title

    window.layer.renderer = Renderer(window.layer, type, listNameValue)

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
        when (type) {
            Diagram.PIE -> canvas.drawPieDiagram(w, h)
            Diagram.COLUMN -> canvas.drawColumnDiagram(w, h)
            Diagram.SCHEDULE -> canvas.drawScheduleDiagram(w, h)
        }
        canvas.drawListNamevalue(w,h)
        layer.needRedraw()
    }

    private fun Canvas.drawPieDiagram(w: Int, h: Int) {
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
            this.drawArc(
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
    }

    private fun Canvas.drawColumnDiagram(w: Int, h: Int) {
        val k = 0.9
        val steps = listNameValue.size
        val startX = w / 3 * (1 - k)
        val startY = h / 2 + h * k / 2
        val dX = 2 * w / 3 * k / (2 * steps - 1)
        val maxSize = h * k

        val maxValue = listNameValue.maxOf { it.value }
        fun getSize(value: Double): Double {
            return value / maxValue * maxSize
        }

        for (i in 0..steps - 1) {
            paint.color = listNameColor[i].color
            this.drawRect(
                Rect(
                    (startX + i * 2 * dX).toFloat(),
                    (startY - getSize(listNameValue[i].value.toDouble())).toFloat(),
                    (startX + i * 2 * dX + dX).toFloat(),
                    (startY).toFloat()
                ), paint
            )
        }
    }

    private fun Canvas.drawScheduleDiagram(w: Int, h: Int) {
        val k = 0.9
        val steps = listNameValue.size
        val startX = w / 3 * (1 - k)
        val startY = h / 2 + h * k / 2
        val dX = 2 * w / 3 * k / (steps - 1)
        val maxSize = h * k
        val eR = 10f

        val maxValue = listNameValue.maxOf { it.value }
        fun getSize(value: Double): Double {
            return value / maxValue * maxSize
        }

        paint.mode = PaintMode.STROKE
        paint.color = Color.makeARGB(255, 0, 0, 0)

        this.drawRect(
            Rect(
                (startX).toFloat(),
                (startY - h * k).toFloat(),
                (startX + 2 * w / 3 * k).toFloat(),
                (startY).toFloat()
            ), paint
        )

        for (i in 0..steps - 1) {
            if (i != steps - 1) {
                paint.color = Color.makeARGB(255, 0, 0, 0)
                this.drawLine(
                    (startX + i * dX).toFloat(),
                    (startY - getSize(listNameValue[i].value.toDouble())).toFloat(),
                    (startX + i * dX + dX).toFloat(),
                    (startY - getSize(listNameValue[i + 1].value.toDouble())).toFloat(),
                    paint
                )
            }
        }

        paint.mode = PaintMode.FILL
        for (i in 0..steps - 1) {
            paint.color = listNameColor[i].color
            this.drawCircle(
                (startX + i * dX).toFloat(),
                (startY - getSize(listNameValue[i].value.toDouble())).toFloat(),
                eR,
                paint
            )
        }
    }

    private fun Canvas.drawListNamevalue(w: Int, h: Int) {
        val k = 0.9
        val steps = listNameValue.size
        val upLeftX = w / 2 + w * k / 6
        val upLeftY = h * (1 - k) / 2
        val d = h * k / steps
        font.setSize(min((2 * d / 3).toFloat(),40f))

        for (i in 0..steps - 1) {
            paint.color = listNameColor[i].color
            this.drawString(
                listNameValue[i].value.toString()+" "+listNameColor[i].name,
                upLeftX.toFloat(),
                (i * d + upLeftY).toFloat() + font.size,
                font,
                paint
            )
        }
    }
}