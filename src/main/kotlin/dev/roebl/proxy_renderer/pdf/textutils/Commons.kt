package dev.roebl.proxy_renderer.pdf.textutils

import org.apache.pdfbox.pdmodel.font.PDFont

internal fun PDFont.widthOf(text: String, fontSize: Float): Float {
    return getStringWidth(text) / 1000.0f * fontSize
}