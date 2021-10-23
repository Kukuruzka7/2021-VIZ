import org.jetbrains.skiko.toImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.*

internal class Test1 {

    @Test
    fun testGetListOfNameValue1() {
        val listNameVal =
            listOf(NameValue("a", 213), NameValue("s", 1231), NameValue("sadlkj", 123), NameValue("lkd", 12312))
        assertContentEquals(listNameVal, getListOfNameValue(File("TestFiles/TestGetListOfNameValue/Test1.txt")))
    }

    @Test()
    fun testGetListOfNameValue2() {
        try {
            getListOfNameValue(File("TestFiles/TestGetListOfNameValue/Test2.txt"))
            assert(false)
        } catch (e: NotCorrectDataInFile) {
            assert(true)
        }
    }

    @Test()
    fun testGetListOfNameValue3() {
        try {
            getListOfNameValue(File("TestFiles/TestGetListOfNameValue/Test3.txt"))
            assert(false)
        } catch (e: NotCorrectDataInFile) {
            assert(true)
        }
    }

    fun testExpectedActual1() {
        val args = arrayOf(
            "schedule_diagram",
            "TestFiles/TestExpectedActual/data1.txt",
            "TestFiles/TestExpectedActual/actual1.png"
        )
        val input = Input(args)
        createWindow(
            "pf-2021-viz",
            input.type,
            input.data,
            input.outputFileName
        )

        val actualImage = ImageIO.read(File("TestFiles/TestExpectedActual/actual1.png")).toImage().encodeToData()
        val expectedImage = ImageIO.read(File("TestFiles/TestExpectedActual/expected1.png")).toImage().encodeToData()

        assertEquals(actualImage, expectedImage)
    }

    fun testExpectedActual2() {
        val args =
            arrayOf("pie_diagram", "TestFiles/TestExpectedActual/data2.txt", "TestFiles/TestExpectedActual/actual2.png")
        val input = Input(args)
        createWindow(
            "pf-2021-viz",
            input.type,
            input.data,
            input.outputFileName
        )

        val actualImage = ImageIO.read(File("TestFiles/TestExpectedActual/actual2.png")).toImage().encodeToData()
        val expectedImage = ImageIO.read(File("TestFiles/TestExpectedActual/expected2.png")).toImage().encodeToData()

        assertEquals(actualImage, expectedImage)
    }

    fun testExpectedActual3() {
        val args = arrayOf(
            "column_diagram",
            "TestFiles/TestExpectedActual/data3.txt",
            "TestFiles/TestExpectedActual/actual3.png"
        )
        val input = Input(args)
        createWindow(
            "pf-2021-viz",
            input.type,
            input.data,
            input.outputFileName
        )

        val actualImage = ImageIO.read(File("TestFiles/TestExpectedActual/actual3.png")).toImage().encodeToData()
        val expectedImage = ImageIO.read(File("TestFiles/TestExpectedActual/expected3.png")).toImage().encodeToData()

        assertEquals(actualImage, expectedImage)
    }

    fun testExpectedActual4() {
        val args = arrayOf(
            "schedule_diagram",
            "TestFiles/TestExpectedActual/data4.txt",
            "TestFiles/TestExpectedActual/actual4.png"
        )
        val input = Input(args)
        createWindow(
            "pf-2021-viz",
            input.type,
            input.data,
            input.outputFileName
        )

        val actualImage = ImageIO.read(File("TestFiles/TestExpectedActual/actual4.png")).toImage().encodeToData()
        val expectedImage = ImageIO.read(File("TestFiles/TestExpectedActual/expected4.png")).toImage().encodeToData()

        assertEquals(actualImage, expectedImage)
    }

    fun testExpectedActual5() {
        val args =
            arrayOf("pie_diagram", "TestFiles/TestExpectedActual/data5.txt", "TestFiles/TestExpectedActual/actual5.png")
        val input = Input(args)
        createWindow(
            "pf-2021-viz",
            input.type,
            input.data,
            input.outputFileName
        )

        val actualImage = ImageIO.read(File("TestFiles/TestExpectedActual/actual5.png")).toImage().encodeToData()
        val expectedImage = ImageIO.read(File("TestFiles/TestExpectedActual/expected5.png")).toImage().encodeToData()

        assertEquals(actualImage, expectedImage)
    }

    fun testExpectedActual6() {
        val args = arrayOf(
            "column_diagram",
            "TestFiles/TestExpectedActual/data6.txt",
            "TestFiles/TestExpectedActual/actual6.png"
        )
        val input = Input(args)
        createWindow(
            "pf-2021-viz",
            input.type,
            input.data,
            input.outputFileName
        )

        val actualImage = ImageIO.read(File("TestFiles/TestExpectedActual/actual6.png")).toImage().encodeToData()
        val expectedImage = ImageIO.read(File("TestFiles/TestExpectedActual/expected6.png")).toImage().encodeToData()

        assertEquals(actualImage, expectedImage)
    }
}
