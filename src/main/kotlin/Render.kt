import org.jetbrains.skija.*
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkiaRenderer
import java.util.*
import kotlin.math.min

class Renderer(val layer: SkiaLayer, val type: Diagram, val listNameValue: List<NameValue>) : SkiaRenderer {
    val typeface = Typeface.makeFromFile("fonts/JetBrainsMono-Regular.ttf")
    val font = Font(typeface, 40f)
    val paint = Paint().apply {
        color = 0xff9BC730L.toInt()
        mode = PaintMode.FILL
        strokeWidth = 1f
    }

    //создание рандомных цветов для соответствующего name val диаграммы
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

        paint.mode = PaintMode.FILL
        when (type) {
            Diagram.PIE -> canvas.drawPieDiagram(w, h, paint)
            Diagram.COLUMN -> canvas.drawColumnDiagram(w, h, paint)
            Diagram.SCHEDULE -> canvas.drawScheduleDiagram(w, h, paint)
        }
        canvas.drawListNamevalue(w, h, paint)

        /* обводка диаграммы
        paint.mode = PaintMode.STROKE
        paint.color = Color.makeARGB(255, 0, 0, 0)
        when (type) {
            Diagram.PIE -> canvas.drawPieDiagram(w, h, paint)
            Diagram.COLUMN -> canvas.drawColumnDiagram(w, h, paint)
           Diagram.SCHEDULE -> canvas.drawScheduleDiagram(w, h, paint)
        }
        */

        //обводка текста
        paint.mode = PaintMode.STROKE
        paint.color = Color.makeARGB(255, 0, 0, 0)
        canvas.drawListNamevalue(w, h, paint)

        layer.needRedraw()
    }

    private fun Canvas.drawPieDiagram(w: Int, h: Int, paint: Paint) {
        val k = 0.9 //коэф для создания отступов от краев

        //координаты круга диаграммы
        val centerX = w / 3
        val centerY = h / 2
        val radius = Math.min(h / 2 * k, w / 3 * k).toInt()

        val totalValue = listNameValue.sumOf { it.value }
        fun getStepSize(value: Double, total: Int): Double {
            return (360 * value) / total
        }

        //отрисовка сегментов диаграммы
        var start = 0.0
        val steps = listNameValue.size
        for (i in 0..steps - 1) {
            val stepSize = getStepSize(listNameValue[i].value.toDouble(), totalValue)
            if (paint.mode != PaintMode.STROKE) { //проверка нужна для того случая, если мы делаем обводку диаграммы
                paint.color = listNameColor[i].color
            }
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

    private fun Canvas.drawColumnDiagram(w: Int, h: Int, paint: Paint) {
        val k = 0.9 //коэф для создания отступов от краев

        val steps = listNameValue.size

        //координаты левого нижнего угла диаграммы
        val startX = w / 3 * (1 - k)
        val startY = h / 2 + h * k / 2
        val dX = 2 * w / 3 * k / (2 * steps - 1)
        val maxSize = h * k

        val maxValue = listNameValue.maxOf { it.value }
        fun getSize(value: Double): Double {
            return value / maxValue * maxSize
        }

        //отрисовка столбцов диаграммы
        for (i in 0..steps - 1) {
            if (paint.mode != PaintMode.STROKE) { //проверка нужна для того случая, если мы делаем обводку диаграммы
                paint.color = listNameColor[i].color
            }
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

    private fun Canvas.drawScheduleDiagram(w: Int, h: Int, paint: Paint) {
        val k = 0.9 //коэф для создания отступов от краев

        val steps = listNameValue.size

        //координаты левого нижнего угла диаграммы
        val startX = w / 3 * (1 - k)
        val startY = h / 2 + h * k / 2
        val dX = 2 * w / 3 * k / (steps - 1)
        val maxSize = h * k
        val eR = 10f //радиус точек

        val maxValue = listNameValue.maxOf { it.value }
        fun getSize(value: Double): Double {
            return value / maxValue * maxSize
        }

        if (paint.mode != PaintMode.STROKE) { //отрисовка без обводки диаграммы
            //отрисовка прямоугольника(координатной плоскости)
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

            //отрисовка линий между точками
            for (i in 0..steps - 1) {
                if (i != steps - 1) {
                    this.drawLine(
                        (startX + i * dX).toFloat(),
                        (startY - getSize(listNameValue[i].value.toDouble())).toFloat(),
                        (startX + i * dX + dX).toFloat(),
                        (startY - getSize(listNameValue[i + 1].value.toDouble())).toFloat(),
                        paint
                    )
                }
            }

            //отрисовка точек
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
        } else {
            //обводка кругов
            for (i in 0..steps - 1) {
                this.drawCircle(
                    (startX + i * dX).toFloat(),
                    (startY - getSize(listNameValue[i].value.toDouble())).toFloat(),
                    eR,
                    paint
                )
            }
        }
    }

    private fun Canvas.drawListNamevalue(w: Int, h: Int, paint: Paint) {
        val k = 0.9  //коэф для создания отступов от краев

        val steps = listNameValue.size

        //координаты левого верхнего угла списка с названиями и значениями
        val upLeftX = w / 2 + w * k / 6
        val upLeftY = h * (1 - k) / 2
        val d = h * k / steps

        font.setSize(min((2 * d / 3).toFloat(), 40f)) //вычисление подходящего шрифта
        val maxLen = listNameValue.maxOf { it.name.length }

        //отрисовка названий: значений
        for (i in 0..steps - 1) {
            if (paint.mode != PaintMode.STROKE) { //проверка нужна для того случая, если мы делаем обводку диаграммы
                paint.color = listNameColor[i].color
            }

            //табулирование
            val outputString = StringBuilder(listNameValue[i].name + ": ")
            repeat(maxLen - listNameValue[i].name.length) {
                outputString.append(" ")
            }
            outputString.append(listNameValue[i].value.toString())

            this.drawString(
                outputString.toString(),
                upLeftX.toFloat(),
                (i * d + upLeftY).toFloat() + font.size,
                font,
                paint
            )
        }
    }
}