import java.io.File

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

        outputFileName = args[2]
    }
}

//получение данных для диаграммы из файла
fun getListOfNameValue(file: File): List<NameValue> {
    val list = mutableListOf<NameValue>()
    for (line in file.readLines()) {
        val name = line.substringAfter(' ')
        val value = line.substringBefore(' ').toIntOrNull()
        if (line == name || name == "" || value == null) {
            throw(NotCorrectDataInFile(line))
        }
        list.add(NameValue(name, value))
    }
    return list
}