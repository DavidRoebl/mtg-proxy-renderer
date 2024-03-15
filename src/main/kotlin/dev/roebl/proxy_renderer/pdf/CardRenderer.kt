package dev.roebl.proxy_renderer.pdf

import dev.roebl.proxy_renderer.Card
import java.io.File

interface CardRenderer {
    val cards: Collection<Card>
    fun renderTo(outFile: File)
}

fun createRenderer(cards: Collection<Card>, mode: RenderMode = RenderMode.A4): CardRenderer = when (mode) {
    RenderMode.SINGLE -> SingleCardRenderer(cards)
    RenderMode.A4 -> A4CardRenderer(cards)
}

enum class RenderMode {
    SINGLE,
    A4
}