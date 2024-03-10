package dev.roebl.proxy_renderer

import com.google.gson.Gson
import dev.roebl.proxy_renderer.pdf.PdfRenderer
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import java.io.File

// todo: create CLI with clikt (https://ajalt.github.io/clikt/)

fun main(args: Array<String>) = runBlocking {
    val cards = File("assets/decklist.txt").readLines()
//    val responses = responsesFromScryfall(cards)
    val responses = responsesFromDisk(cards)
    responses.forEach { card ->
        createPdf(card)
    }
}

private fun createPdf(card: Card) {
    val file = File("assets/pdfs/${card.name}.pdf")
    val renderer = PdfRenderer(card)
    renderer.renderTo(file)
}

private val client = HttpClient(CIO)
private val gson = Gson()

private suspend fun responsesFromScryfall(cards: List<String>): List<Card> {
    return cards.map { card ->
        Thread.sleep(100)
        loadDetails(card)
    }
}

private fun responsesFromDisk(cards: List<String>) : List<Card> {
    return cards.map { card ->
        val json = File("assets/responses/$card").readText()
        gson.fromJson(json, Card::class.java)
    }
}

private suspend fun loadDetails(card: String): Card {
    val response = client.get("https://api.scryfall.com/cards/named") {
        url {
            parameters.append("exact", card)
        }
    }
    val jsonResponse = response.bodyAsText()
    File("assets/responses/$card").writeText(jsonResponse)
    return gson.fromJson(jsonResponse, Card::class.java)
}