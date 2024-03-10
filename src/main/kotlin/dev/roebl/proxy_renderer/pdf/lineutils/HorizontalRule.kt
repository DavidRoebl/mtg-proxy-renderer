package dev.roebl.proxy_renderer.pdf.lineutils

import dev.roebl.proxy_renderer.pdf.Settings
import org.apache.pdfbox.pdmodel.PDPageContentStream

fun PDPageContentStream.horizontalRule(yPos: Float, intensity: Float = 1f) {
    line(0f, yPos, Settings.pageWidth, yPos, intensity)
}
