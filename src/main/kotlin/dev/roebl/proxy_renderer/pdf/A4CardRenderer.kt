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
import kotlin.math.max
import kotlin.math.min

private const val POINTS_PER_MM = 72 / (10 * 2.54f)

private val pageSize = PDRectangle.A4
private val cardWidth = 60 * POINTS_PER_MM
private val cardHeight = 85 * POINTS_PER_MM

private const val SPACE_BETWEEN_CARDS_AND_PAGE_BORDER = 9 * POINTS_PER_MM
private const val SPACE_BETWEEN_CARDS = 1 * POINTS_PER_MM
private const val MARGIN = 10f

private const val CARDS_PER_ROW = 3
private const val ROWS_PER_PAGE = 3
private const val CARDS_PER_PAGE = CARDS_PER_ROW * ROWS_PER_PAGE

private const val TEXT_SIZE_LARGE = 12f
private const val TEXT_SIZE_SMALL = 10f


class A4CardRenderer(
    override val cards: Collection<Card>
) : CardRenderer {
    override fun renderTo(outFile: File) {
        Settings.pageWidth = pageSize.width
        Settings.pageHeight = pageSize.height
        Settings.cardWidth = cardWidth
        Settings.cardHeight = cardHeight

        val matrix = Matrix.getTranslateInstance(0f, pageSize.height)

        PDDocument().use { document ->
            val font = loadFonts(document)
            val maxCostWidth = font.widthOf("{G}{G}{G}", TEXT_SIZE_SMALL)
            println("max cost width ({M}{M}{M}): $maxCostWidth")
            cards.chunked(CARDS_PER_PAGE).forEach { pageCards ->
                val page = PDPage(pageSize)
                document.addPage(page)
                PDPageContentStream(document, page).use { stream ->
                    stream.apply {
                        // page setup
                        transform(matrix)

                        var cardOriginY = SPACE_BETWEEN_CARDS_AND_PAGE_BORDER
                        pageCards.chunked(CARDS_PER_ROW).forEach { rowCards ->
                            var cardOriginX = SPACE_BETWEEN_CARDS_AND_PAGE_BORDER
                            horizontalRule(
                                yPos = 45f + cardOriginY
                            )
                            rowCards.forEach { card ->

                                rectangle(cardOriginX, cardOriginY, cardWidth, cardHeight)

                                val fullCostWidth = font.widthOf(card.manaCost, TEXT_SIZE_SMALL)
                                val costWidth = min(fullCostWidth, maxCostWidth)
                                println("card ${card.name} has a cost of ${card.manaCost}, which takes $fullCostWidth to print")

                                // top matter
                                multilineText(
                                    content = card.name,
                                    xPos = MARGIN + cardOriginX,
                                    yPos = MARGIN + cardOriginY,
                                    width = cardWidth - 3 * MARGIN - costWidth,
//                                    textSize = if (card.name.contains("Omnath")) TEXT_SIZE_SMALL else TEXT_SIZE_LARGE
                                    textSize = TEXT_SIZE_LARGE
                                )
                                multilineText(
                                    content = card.manaCost,
                                    xPos = cardWidth - MARGIN + cardOriginX,
                                    yPos = MARGIN + 1 + cardOriginY,
                                    align = Align.RIGHT,
                                    width = costWidth,
                                    textSize = TEXT_SIZE_SMALL,
                                    splitToWords = { text ->
                                        val blocks = mutableListOf<String>()
                                        val current = StringBuilder()
                                        text.forEach { char ->
                                            current.append(char)
                                            if (char == '}') {
                                                blocks.add(current.toString())
                                                current.clear()
                                            }
                                        }
                                        blocks.toTypedArray()
                                    },
                                    wordsToString = { words ->
                                        if (words.isEmpty()) {
                                            ""
                                        } else {
                                            words.reduce { first, second ->
                                                "$first$second"
                                            }
                                        }
                                    }
                                )


                                // type line(s)
                                var yPos = 50f
                                yPos += multilineText(
                                    content = card.types,
                                    xPos = MARGIN + cardOriginX,
                                    yPos = yPos + cardOriginY,
                                    textSize = TEXT_SIZE_SMALL,
                                    width = cardWidth - 2 * MARGIN
                                ) + 5f

                                multilineText(
                                    content = card.oracle,
                                    xPos = MARGIN + cardOriginX,
                                    yPos = yPos + cardOriginY,
                                    textSize = TEXT_SIZE_SMALL,
                                    width = cardWidth - 2 * MARGIN
                                )

                                if (card.power != null && card.toughness != null) {
                                    text(
                                        content = "${card.power}/${card.toughness}",
                                        yPos = cardHeight - MARGIN - TEXT_SIZE_LARGE - 3f + cardOriginY,
                                        xPos = cardWidth - MARGIN + cardOriginX,
                                        align = Align.RIGHT,
                                        textSize = TEXT_SIZE_LARGE
                                    )
                                }

                                cardOriginX += cardWidth + SPACE_BETWEEN_CARDS
                            }
                            cardOriginY += cardHeight + SPACE_BETWEEN_CARDS
                            cardOriginX = SPACE_BETWEEN_CARDS_AND_PAGE_BORDER
                        }
                    }
                }
            }
            document.save(outFile)
        }
    }

    private fun loadFonts(document: PDDocument): PDType0Font {
        // get from
        //  - https://github.com/AlexandreArpin/mtg-font
        //  - https://alexandrearpin.com/mtg-font/index.html
        val font1 = PDType0Font.load(document, File("assets/font/mplantin/Mplantin.ttf"))
        Settings.font = font1
        return font1
    }
}