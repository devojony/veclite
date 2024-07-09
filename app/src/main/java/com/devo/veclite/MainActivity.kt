package com.devo.veclite

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.devo.veclite.ui.theme.VecliteTheme
import com.devo.veclite.util.AppSQLite
import com.devo.extension.VecliteLib
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.gson.gson
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val inputState by lazy {
        mutableStateOf("")
    }

    private val contentList by lazy {
        mutableStateListOf<ContentItem>()
    }

    private val appSqlite by lazy {
        AppSQLite(this, "vectors.db", null, 1)
    }

    private val searchType by lazy {
        mutableStateOf(SearchType.L2)
    }

    private val client by lazy {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                gson()
            }
            defaultRequest {
                url("http://ollama.devo.top:99/")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        VecliteLib()
        setContent {
            VecliteTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val (filter, onFilterChange) = remember { searchType }

                    MainScreen(
                        inputState = inputState,
                        contentList = contentList.toList(),
                        onAddContent = ::addContent,
                        onSearchContent = ::searchContent,
                        onRefresh = ::refresh,
                        filter = filter,
                        onFilterChange = onFilterChange

                    )
                }
            }
        }
    }

    private fun addContent(content: String) {

        lifecycleScope.launch {
            val result = client.post("api/embeddings") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("model" to "mxbai-embed-large", "prompt" to content))
            }.body<EmbeddingResult>()

            val vec = result.embedding.toDoubleArray()
            val item = ContentItem(content = content, vec = vec)

            val ids = appSqlite.writableDatabase?.insert("vectors", null, item.toContentValues())

            contentList.add(item.copy(id = ids))
        }
    }

    private fun searchContent(content: String) {
        lifecycleScope.launch {
            val result = client.post("api/embeddings") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("model" to "mxbai-embed-large", "prompt" to content))
            }.body<EmbeddingResult>()

            val vec = (result.embedding.toDoubleArray()).joinToString(",")

            val func = when(searchType.value) {
                SearchType.NIP -> {
                    "nip_dis(vec, ?) desc"
                }
                SearchType.COS -> {
                    "cos_dis(vec, ?) desc"
                }
                else -> {
                    "l2_dis(vec, ?)"
                }
            }


            val cursor = appSqlite.readableDatabase?.rawQuery(
                "select * from vectors order by $func limit 5;",
                arrayOf(vec)
            )

            cursor?.let {
                contentList.clear()
                it.move(-1)
                while (it.moveToNext()) {
                    contentList.add(
                        ContentItem(
                            id = it.getLong(0),
                            content = it.getString(1),
                            vec = it.getString(2).split(",").map { d ->
                                d.toDouble()
                            }.toDoubleArray()
                        ),
                    )
                }
            }
        }
    }

    private fun refresh() {
        contentList.clear()
        appSqlite.readableDatabase?.rawQuery(
            "select * from vectors;",
            null
        )?.let {
            it.move(-1)
            while (it.moveToNext()) {
                contentList.add(
                    ContentItem(
                        id = it.getLong(0),
                        content = it.getString(1),
                        vec = it.getString(2).split(",").map { d ->
                            d.toDouble()
                        }.toDoubleArray()
                    ),
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        refresh()
    }

    override fun onDestroy() {
        super.onDestroy()
        appSqlite.close()
        client.close()
    }

}
