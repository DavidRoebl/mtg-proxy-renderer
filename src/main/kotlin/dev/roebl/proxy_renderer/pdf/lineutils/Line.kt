package dev.roebl.proxy_renderer.pdf.lineutils

import org.apache.pdfbox.pdmodel.PDPageContentStream

fun PDPageContentStream.line(xFrom: Float, yFrom: Float, xTo: Float, yTo: Float, thickness: Float = 1f) {
    setLineWidth(thickness)
    moveTo(xFrom, yFrom * -1)
    lineTo(xTo, yTo * -1)
    stroke()
}
