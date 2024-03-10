package dev.roebl.proxy_renderer.pdf.textutils

import dev.roebl.proxy_renderer.pdf.Align
import dev.roebl.proxy_renderer.pdf.Settings
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDFont
import kotlin.math.abs
import kotlin.math.min


internal fun PDPageContentStream.text(
    content: String,
    textSize: Float = 12f,
    lineSpace: Float = 2f,
    align: Align = Align.LEFT,
    intensity: Float = 1f,
    xPos: Float,
    yPos: Float
): Float {
    val strings = content.split("\n")
    val font = Settings.font
    val _yPos = yPos * -1 - textSize
    val _xPos = when (align) {
        Align.LEFT -> xPos
        Align.RIGHT -> xPos - font.widthOf(content, textSize)
        Align.CENTER -> xPos - font.widthOf(content, textSize) / 2
    }
    beginText()
    setFont(font, textSize)
    setNonStrokingColor(1 - intensity, 1 - intensity, 1 - intensity)
    newLineAtOffset(_xPos, _yPos)
    setLeading(textSize + lineSpace)
    strings.forEach {
        showText(it)
        newLine()
    }
    setNonStrokingColor(0f, 0f, 0f)
    endText()
    return strings.size * (textSize + lineSpace)
}

internal fun PDPageContentStream.multilineText(
    content: String,
    textSize: Float = 12f,
    lineSpace: Float = 2f,
    paragraphSpace: Float = 10f,
    align: Align = Align.LEFT,
    intensity: Float = 1f,
    xPos: Float,
    yPos: Float,
    width: Float
): Float {
    val blocks = content.split("\n")
    val font = Settings.font
    val _yPosStart = yPos * -1 - textSize
    var _yPos = _yPosStart
    val _xPos = when (align) {
        Align.LEFT -> xPos
        Align.RIGHT -> xPos - font.widthOf(content, textSize)
        Align.CENTER -> xPos - font.widthOf(content, textSize) / 2
    }

    blocks.forEach { blockContent ->
        val words = blockContent.split(" ").toTypedArray()
        var startIndex = 0
        val lines = mutableListOf<String>()
        while (startIndex <= words.size) {
            val lineSize = findElementsThatFit(
                input = words,
                font = font,
                maxWidth = width,
                startIndex = startIndex,
                fontSize = textSize
            )
            if (lineSize == 0) {
                break
            }
            val line = words.copyOfRange(startIndex, startIndex + lineSize).recombine()
            lines.add(line)
            startIndex += lineSize
        }

        beginText()
        setFont(font, textSize)
        setNonStrokingColor(1 - intensity, 1 - intensity, 1 - intensity)
        newLineAtOffset(_xPos, _yPos)
        setLeading(textSize + lineSpace)
        lines.forEach {
            showText(it)
            newLine()
        }
        setNonStrokingColor(0f, 0f, 0f)
        endText()

        _yPos -= (textSize + lineSpace) * lines.size + paragraphSpace
    }
    return abs(_yPos - _yPosStart)
}


private fun findElementsThatFit(
    input: Array<String>,
    font: PDFont,
    fontSize: Float = 9f,
    maxWidth: Float,
    startIndex: Int = 0,
    indent: String = ""
): Int {
    val widthOfIndent = font.widthOf(indent, 9f)

    var toIndex = startIndex
    do {
        toIndex += 1
        val string = input.copyOfRange(startIndex, min(toIndex, input.size)).recombine()
        val width = font.widthOf(string, fontSize) + widthOfIndent

    } while (toIndex <= input.size && width <= maxWidth)

    return toIndex - startIndex - 1
}


private fun Array<String>.recombine(delimiter: String = " "): String {
    return if (isEmpty()) {
        return ""
    } else {
        reduce { first, second ->
            "$first$delimiter$second"
        }
    }
}