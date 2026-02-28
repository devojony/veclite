package com.devo.veclite.llama

enum class Model(
    val path: String,
    private val userTemplate: String,
    private val assistantTemplate: String,
    private val parseAssistant: String = "",
) {
    MiniCPM("MiniCPm", "%s", "%s"),
    Phi3(
        "Phi-3-mini-4k-instruct-q4.gguf",
        "<|user|>%s<|end|>",
        "<|assistant|>%s",
        "<|assistant|>"
    );

    fun createUserMessage(src: String): String = userTemplate.format(src)
    fun createAssistantMessage(src: String): String = assistantTemplate.format(src)
    fun parseResponse(src: String) = src.replace(parseAssistant, "")
}

fun main() {

    Model.entries.forEach {
        println(it)
    }
}