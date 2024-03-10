package dev.roebl.proxy_renderer

import com.google.gson.annotations.SerializedName

data class Card (
    @SerializedName("name") val name: String,
    @SerializedName("scryfall_uri") val uri: String,
    @SerializedName("mana_cost") val manaCost: String,
    @SerializedName("type_line") val types: String,
    @SerializedName("oracle_text") val oracle: String,
    @SerializedName("power") val power: String?,
    @SerializedName("toughness") val toughness: String?
)