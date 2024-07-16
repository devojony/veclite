package com.devo.veclite

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.devo.extension.Veclite
import com.devo.extension.Veclite.Companion.SearchType
import com.devo.veclite.ui.theme.VecliteTheme
import com.devo.veclite.util.Ollama
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private val tag = "MainActivity"

    private val inputState by lazy {
        mutableStateOf("")
    }

    private val contentList by lazy {
        mutableStateListOf<ContentItem>()
    }

    private val searchType = MutableStateFlow(SearchType.L2)

    private val veclite by lazy {
        Veclite(applicationContext, "vectors") {
            Ollama.embedText(it)
        }
    }

    private val gson by lazy {
        Gson()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VecliteTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val filter = searchType.collectAsState()

                    MainScreen(
                        inputState = inputState,
                        contentList = contentList.toList(),
                        onAddContent = ::addContent,
                        onSearchContent = ::searchContent,
                        onRefresh = ::refresh,
                        filter = filter.value,
                        onFilterChange = {
                            searchType.tryEmit(it)
                        }
                    )
                }
            }
        }
    }

    private fun addContent(content: String) {

        lifecycleScope.launch {
            val item = ContentItem(content = content)
            val id = veclite.addDocument(content, null)
            contentList.add(item.copy(id = id))
        }
    }

    private fun searchContent(content: String) {
        lifecycleScope.launch {
            val list = veclite.queryVectors(content, searchType.value)

            Log.i(tag, "searchContent: ${list.size}")

            if (list.isEmpty()) return@launch

            contentList.clear()
            gson.toJson(list).let {
                contentList.addAll(gson.fromJson(it, Array<ContentItem>::class.java).asList())
            }

        }
    }

    private fun refresh() {
        contentList.clear()

        contentList.addAll(veclite.queryDocuments().map {
            gson.toJson(it).run {
                gson.fromJson(this, ContentItem::class.java)
            }
        })
    }


    override fun onStart() {
        super.onStart()
        refresh()
    }

}
