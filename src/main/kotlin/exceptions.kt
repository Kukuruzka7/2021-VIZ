class NotCorrectInputData() :
    Exception("Неверный формат ввода данных. Ожидается: diagramType <dataFileName> <output.png>") {
}

class NotCorrectDiagramType(typename: String) :
    Exception("Диаграмма $typename не поддерживается.") {
}

class FileIsNotExist(filename: String) :
    Exception("Файла $filename не существует.") {
}

class NotCorrectDataInFile(line: String) :
    Exception("Файл содержит не правильный формат данных диаграммы.\n$line\nОжидается: value name") {
}

class SaveInFileImpossible(filename: String) :
    Exception("Не возможно сохранить диаграмму в $filename.") {
}