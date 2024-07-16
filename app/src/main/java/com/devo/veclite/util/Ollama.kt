package com.devo.veclite.util

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.timeout
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.gson.gson

data class EmbeddingResult(val embedding: List<Double>)

object Ollama {

    //    private const val embeddingModel = "nomic-embed-text"
    private const val embeddingModel = "rjmalagon/gte-qwen2-1.5b-instruct-embed-f16"

    private val client by lazy {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                gson()
            }
            install(HttpTimeout)
            defaultRequest {
                url("http://ollama.devo.top:99/")
            }
        }
    }

    suspend fun embedText(text: String): DoubleArray {

        val result = client.post("api/embeddings") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("model" to embeddingModel, "prompt" to text))
            timeout {
                requestTimeoutMillis = 10 * 60 * 1000
            }
        }.body<EmbeddingResult>()

        return result.embedding.toDoubleArray()
    }

}