package dev.roebl.proxy_renderer.pdf

import org.apache.pdfbox.pdmodel.font.PDFont

object Settings {
    var pageWidth: Float = 0f
    var pageHeight: Float = 0f
    lateinit var font: PDFont
}
