package com.devo.veclite.llama

import android.llama.cpp.LLamaAndroid
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devo.veclite.VecliteApp
import com.devo.veclite.entity.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.io.File

//const val modelName = "MiniCPM-2B-dpo-fp16-gguf.gguf"
const val modelName = "MiniCPM-2B-dpo-q4km-gguf.gguf"
//const val modelName = "Phi-3-mini-4k-instruct-q4.gguf"

class LLamaViewModel : ViewModel() {
    private val tag = "LLamaViewModel"
    private val _uiToast = MutableStateFlow("")
    val toast = _uiToast.distinctUntilChanged { old, new -> old == new }
    private val llama = LLamaAndroid()
    private val messages = mutableListOf(
        Message(isSelf = true, content = "Hi, I am LLama."),
        Message(isSelf = false, content = "Hi, I am LLama."),
    )
    private val msgListFlow = MutableStateFlow<List<Message>>(messages)
    val messagesFlow = msgListFlow

    val isLoaded = MutableStateFlow(false)


    fun tips(text: String) {
        _uiToast.value = text
    }

    fun loadModel() {
        viewModelScope.launch(Dispatchers.IO) {
            val modelFile = File(VecliteApp.modelDir, modelName)
            if (modelFile.exists())
                try {
                    Log.i(tag, "开始加载模型: ${modelFile.absolutePath}")
                    llama.load(modelFile.absolutePath)
                    isLoaded.emit(true)
                    tips("加载模型成功")
                } catch (e: IllegalStateException) {
                    Log.e(tag, "加载模型失败", e)
                    tips("加载模型失败")
                }
        }

    }

    fun sendMessage(message: String) {

        msgListFlow.value += Message(isSelf = true, content = message)
        msgListFlow.value += Message(isSelf = false, content = "")
        // 孔子的核心思想是什么
        viewModelScope.launch {
            llama.send("<用户>${message}<AI>")
                .catch {
                    tips("send() failed\n$it")
                    msgListFlow.value = msgListFlow.value.dropLast(1)
                }
                .collect {
                    val last = msgListFlow.value.last()
                    msgListFlow.value = msgListFlow.value.dropLast(1) + last.copy(content = last.content + it)
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch { llama.unload() }
    }

}
