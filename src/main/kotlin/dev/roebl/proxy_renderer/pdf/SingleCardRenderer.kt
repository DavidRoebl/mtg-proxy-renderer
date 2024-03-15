package dev.roebl.proxy_renderer.pdf

import dev.roebl.proxy_renderer.Card
import dev.roebl.proxy_renderer.pdf.lineutils.horizontalRule
import dev.roebl.proxy_renderer.pdf.lineutils.rectangle
import dev.roebl.proxy_renderer.pdf.textutils.multilineText
import dev.roebl.proxy_renderer.pdf.textutils.text
import dev.roebl.proxy_renderer.pdf.textutils.widthOf
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDType0Font
import org.apache.pdfbox.util.Matrix
import java.io.File

private const val POINTS_PER_INCH = 72 // from decompiled PDRectangle class
private const val width = 2.5f * POINTS_PER_INCH
private const val height = 3.5f * POINTS_PER_INCH
private val cardSize = PDRectangle(width, height)

private const val MARGIN = 10f

class SingleCardRenderer (
    override val cards: Collection<Card>
) : CardRenderer {
    override fun renderTo(outFile: File) {
        Settings.pageWidth = width
        Settings.pageHeight = height

        PDDocument().use { document ->
            loadFonts(document)
            cards.forEach { card ->

                val page = PDPage(cardSize)
                document.addPage(page)
                val matrix = Matrix.getTranslateInstance(0f, height)
                val font = Settings.font
                val costWidth = font.widthOf(card.manaCost, 10f)

                PDPageContentStream(document, page).use { stream ->
                    stream.apply {
                        // page setup
                        transform(matrix)
                        rectangle(0f, 0f, width, height)

                        // top matter
                        multilineText(
                            content = card.name,
                            xPos = MARGIN,
                            yPos = MARGIN,
                            width = width - 2 * MARGIN - costWidth
                        )
                        text(
                            content = card.manaCost,
                            xPos = width - MARGIN,
                            yPos = MARGIN + 1,
                            align = Align.RIGHT,
                            textSize = 10f
                        )
                        horizontalRule(
                            yPos = 45f
                        )

                        // type line(s)
                        var yPos = 50f
                        yPos += multilineText(
                            content = card.types,
                            xPos = MARGIN,
                            yPos = yPos,
                            textSize = 10f,
                            width = width - 2 * MARGIN
                        ) + 5f

                        multilineText(
                            content = card.oracle,
                            xPos = MARGIN,
                            yPos = yPos,
                            textSize = 10f,
                            width = width - 2 * MARGIN
                        )

                        if (card.power != null && card.toughness != null) {
                            text(
                                content = "${card.power}/${card.toughness}",
                                yPos = height - MARGIN - 12f - 3f,
                                xPos = width - MARGIN,
                                align = Align.RIGHT,
                                textSize = 12f
                            )
                        }
                    }
                }

            }
            document.save(outFile)
        }
    }

    private fun loadFonts(document: PDDocument) {
        // get from
        //  - https://github.com/AlexandreArpin/mtg-font
        //  - https://alexandrearpin.com/mtg-font/index.html
        val font1 = PDType0Font.load(document, File("assets/font/mplantin/Mplantin.ttf"))
        Settings.font = font1
    }
}