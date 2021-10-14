import java.io.File
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


}
