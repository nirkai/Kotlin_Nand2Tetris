package parsing

import parsing.Statements.Companion.statments
import java.io.File
import java.io.FileWriter

class ProgramStructure {

    companion object {
        var tab : Int = 0
        @JvmStatic var inputFile = ""

        fun tokenizing() {

            var filePath = System.getProperty("user.dir")
            filePath += "//test"
            File(filePath).walk().forEach { fileJack ->
                if (fileJack.isFile && fileJack.name.contains("T.xml")) {
                    inputFile = fileJack.readText()
                    var g = fileJack.readText()

                    /*val lineList = mutableListOf<String>()
                    var hackProgramString = ""

                    fileJack.bufferedReader().useLines { lines -> lines.forEach { lineList.add(it) } }
                    lineList.forEach{
                        var s = it.replace(" *<.*?> *".toRegex(), "")
                        println(s)
                    }*/


                    var outStream = FileWriter(filePath + "\\" + fileJack.name.removeSuffix("T.xml") + ".xml")
                    outStream.write(parseClass())
                    outStream.close()
                }
            }
        }

        fun parseClass() : String {
            getNextToken()
            tab++
            var output = "<class>\n" +
                    getNextToken() + getNextToken() + getNextToken()
            output += parseClassVarDec()

            while (checkNextToken().contains("method|function|constructor".toRegex())){
                output += subroutineDec()
            }
            output += getNextToken()
            output += "</class>\n"
            return  output
        }

        fun getNextToken() : String {
            var s = inputFile.substring(0, inputFile.indexOf("\n")+1)
            var g = inputFile.substring(inputFile.indexOf("\n")+1)
            inputFile = g
            return s
        }

        fun checkNextToken() : String {
            return inputFile.substring(0, inputFile.indexOf("\n"))
        }

        fun parseClassVarDec() : String{
            var output = ""
            while (checkNextToken().contains("static|field".toRegex())){
                output += "<classVarDec>\n"
                output += getNextToken() + getNextToken() + getNextToken()
                while (checkNextToken().contains(",")){
                    output += getNextToken() + getNextToken()
                }
                output += getNextToken() +      // ;
                        "</classVarDec>\n"
            }
            return output
        }

        fun subroutineDec() :String {
            var output = "<subroutineDec>\n"
                output += getNextToken() + getNextToken() + getNextToken() +     // method|function|constructor void|type subroutineName
                    getNextToken() + parameterList() + getNextToken() +          // ( parameterList )
                    subroutineBody() + "</subroutineDec>\n"
            return output
        }

        fun parameterList() :String{
            var parm = "<parameterList>\n"
            if (checkNextToken().contains("void|int|boolean|char|identifier".toRegex())){
                parm += getNextToken() + getNextToken()     //  type varName
                while (checkNextToken().contains(","))
                    parm += getNextToken() + getNextToken() + getNextToken()    //  , type varName
            }
            return parm + "</parameterList>\n"
        }

        fun subroutineBody():String{
            var body = "<subroutineBody>\n" + getNextToken()    //  {
            while (checkNextToken().contains("var"))
                body += varDec()
            body += statments()
            body += getNextToken() + "</subroutineBody>\n"
            return body
        }

        fun varDec() :String{
            var dec = "<varDec>\n" + getNextToken() + getNextToken() + getNextToken()  //  var type varName
            while (checkNextToken().contains(","))
                dec += getNextToken() + getNextToken()                  // , varName
            dec += getNextToken() + "</varDec>\n"                       // ;
            return dec
        }

    }
}