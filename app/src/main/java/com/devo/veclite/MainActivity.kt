package com.devo.veclite

import android.os.Bundle
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
import com.devo.extension.VecliteLib
import com.devo.veclite.ui.theme.VecliteTheme
import com.devo.veclite.util.AppSQLite
import com.devo.veclite.util.Ollama
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    private val searchType = MutableStateFlow(SearchType.L2)

    private val sqlFunc = searchType.map {
        when (it) {
            SearchType.NIP -> "nip_dis(vec, ?)"
            SearchType.COS -> "cos_dis(vec, ?)"
            else -> "l2_dis(vec, ?)"
        }
    }

    private val sqlSort = searchType.map {
        when (it) {
            SearchType.NIP, SearchType.COS -> "DESC"
            else -> "ASC"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        VecliteLib()
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
            val result = Ollama.embedText(content)

            val vec = result.toDoubleArray()
            val item = ContentItem(content = content, vec = vec)

            val ids = appSqlite.writableDatabase?.insert("vectors", null, item.toContentValues())

            contentList.add(item.copy(id = ids))
        }
    }

    private fun searchContent(content: String) {
        lifecycleScope.launch {
            val vec = Ollama.embedText(content).joinToString(",")

            val func = sqlFunc.stateIn(this).value
            val sort = sqlSort.stateIn(this).value

            val cursor = appSqlite.readableDatabase?.rawQuery(
                "select id, content, vec, $func as score from vectors order by score $sort limit 5;",
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
                            }.toDoubleArray(),
                            score = it.getDouble(3)
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
    }

}
