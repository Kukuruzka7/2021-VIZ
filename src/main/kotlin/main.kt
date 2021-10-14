import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.swing.Swing
import org.jetbrains.skija.*
import org.jetbrains.skiko.SkiaWindow
import java.awt.Dimension
import java.nio.ByteBuffer
import java.nio.channels.ByteChannel
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import javax.swing.WindowConstants
import kotlin.io.path.Path

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

fun main(args: Array<String>) {
    try {
        val input = Input(args)
        createWindow(
            "pf-2021-viz",
            input.type,
            input.data,
            input.outputFileName
        )
    } catch (e: Exception) {
        println(e.message)
    }
}

//создание окна и сохранение png
fun createWindow(title: String, type: Diagram, listNameValue: List<NameValue>, outputFileName: String) =
    runBlocking(Dispatchers.Swing) {
        val window = SkiaWindow()
        window.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
        window.title = title

        window.layer.renderer = Renderer(window.layer, type, listNameValue)

        window.preferredSize = Dimension(900, 600)
        window.minimumSize = Dimension(100, 100)
        window.pack()
        window.layer.awaitRedraw()
        window.isVisible = true

        val screenshot: Bitmap? = window.layer.screenshot()
        val image: Image = Image.makeFromBitmap(screenshot!!)
        val pngData: Data? = image.encodeToData(EncodedImageFormat.PNG)
        val pngBytes: ByteBuffer? = pngData?.toByteBuffer()
        try {
            val path = Path(outputFileName)
            val channel: ByteChannel = Files.newByteChannel(
                path,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE
            )
            channel.write(pngBytes)
            channel.close()
        } catch (e: Exception) {
            println("Не возможно сохранить диаграмму в файл.")
        }
    }
