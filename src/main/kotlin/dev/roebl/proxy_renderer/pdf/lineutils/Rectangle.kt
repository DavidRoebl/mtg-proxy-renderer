package dev.roebl.proxy_renderer.pdf.lineutils

import org.apache.pdfbox.pdmodel.PDPageContentStream

fun PDPageContentStream.rectangle(
    xPos: Float,
    yPos: Float,
    width: Float,
    height: Float,
    thickness: Float = 1f
) {
    line(xPos, yPos, xPos + width, yPos, thickness)
    line(xPos + width, yPos, xPos + width, yPos + height, thickness)
    line(xPos + width, yPos + height, xPos, yPos + height, thickness)
    line(xPos, yPos + height, xPos, yPos, thickness)
}
