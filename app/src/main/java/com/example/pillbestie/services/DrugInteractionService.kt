package com.example.pillbestie.services

import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class DrugInteractionService {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    private suspend fun getRxcui(medicineName: String): String? {
        return try {
            val response: HttpResponse = client.get("https://rxnav.nlm.nih.gov/REST/rxcui.json?name=$medicineName")
            val responseBody = response.bodyAsText()
            if (responseBody.contains("idGroup")) {
                val rxcuiResponse = Json.decodeFromString<RxcuiResponse>(responseBody)
                rxcuiResponse.idGroup.rxnormId?.firstOrNull()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("DrugInteractionService", "Failed to get RXCUI for $medicineName", e)
            null
        }
    }

    suspend fun getInteractions(medicineNames: List<String>): List<String> {
        val rxcuis = medicineNames.mapNotNull { getRxcui(it) }
        if (rxcuis.size < 2) {
            return emptyList()
        }

        return try {
            val rxcuiString = rxcuis.joinToString("+")
            val response: HttpResponse = client.get("https://rxnav.nlm.nih.gov/REST/interaction/list.json?rxcuis=$rxcuiString")
            val responseBody = response.bodyAsText()

            if (responseBody.contains("fullInteractionTypeGroup")) {
                val interactionResponse = Json.decodeFromString<InteractionResponse>(responseBody)
                interactionResponse.fullInteractionTypeGroup.flatMap { it.fullInteractionType }.map { it.interactionPair.first().description }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("DrugInteractionService", "Failed to get interactions", e)
            emptyList()
        }
    }
}

@Serializable
data class RxcuiResponse(val idGroup: IdGroup)

@Serializable
data class IdGroup(val rxnormId: List<String>? = null)

@Serializable
data class InteractionResponse(val fullInteractionTypeGroup: List<FullInteractionTypeGroup>)

@Serializable
data class FullInteractionTypeGroup(val fullInteractionType: List<FullInteractionType>)

@Serializable
data class FullInteractionType(val interactionPair: List<InteractionPair>)

@Serializable
data class InteractionPair(val description: String)
