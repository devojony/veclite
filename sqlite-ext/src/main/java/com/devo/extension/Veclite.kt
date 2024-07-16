package com.devo.extension

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.core.database.getStringOrNull
import com.devo.extension.utils.AppSQLite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.json.JSONObject

class Veclite(
    context: Context,
    private val collectionName: String = "default",
    val embeddingFactory: suspend (String) -> DoubleArray
) {

    private val appSqlite by lazy {
        AppSQLite(context, "$collectionName.db", null, 1)
    }

    private val scope = CoroutineScope(Dispatchers.IO + Job())

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

    companion object {
        private val tag = Veclite::class.java.simpleName

        init {
            System.loadLibrary("veclite")
        }

        enum class SearchType(val title: String) {
            L2("L2"),
            COS("COS"),
            NIP("NIP"),
        }
    }

    suspend fun addDocument(content: String, extra: Map<String, Any>?): Long {

        val cv = ContentValues()
        cv.put("content", content)
        cv.put("vec", embeddingFactory(content).joinToString(","))
        cv.put("extra", if (extra != null) JSONObject(extra).toString() else null)
        val id = appSqlite.writableDatabase?.insert("vectors", null, cv)
        return id ?: 0
    }

    suspend fun queryVectors(content: String, type: SearchType = SearchType.L2): List<Map<String, Any?>> {

        searchType.tryEmit(type)
        val vec = embeddingFactory(content)

        val func = sqlFunc.stateIn(scope).value
        val sort = sqlSort.stateIn(scope).value

        val cursor = appSqlite.readableDatabase?.rawQuery(
            "select id, content, vec, extra, $func as score from vectors order by score $sort limit 5;",
            arrayOf(vec.joinToString(","))
        )
        // 身份证信息被公布
        return cursor?.let { c ->
            buildList {
                while (c.moveToNext()) {
                    add(
                        mapOf(
                            "id" to c.getLong(0),
                            "content" to c.getString(1),
                            "vec" to c.getString(2).split(",").map { d ->
                                d.toDouble()
                            }.toDoubleArray(),
                            "extra" to c.getStringOrNull(3)?.let { str ->
                                JSONObject(str).run {
                                    keys().asSequence().associateWith { get(it) }
                                }
                            },
                            "score" to c.getDouble(4)
                        )
                    )
                }
            }
        } ?: listOf()

    }

    fun queryDocuments(id: Long? = null, count: Int = 10): List<Map<String, Any?>> {
        val sql = buildString {
            append("select id, content, extra from vectors")
            if (id != null) {
                append(" where id = ?")
            }
            append(" limit $count")
            append(";")
        }

        val cursor = appSqlite.readableDatabase?.rawQuery(
            sql,
            if (id != null) arrayOf(id.toString()) else null
        )
        Log.i(tag, "queryDocuments: result length is ${cursor?.count ?: 0}")
        return cursor?.let { c ->
            buildList {
                while (c.moveToNext()) {
                    add(
                        mapOf(
                            "id" to c.getLong(0),
                            "content" to c.getString(1),
                            "extra" to c.getStringOrNull(2)?.let { str ->
                                JSONObject(str).run {
                                    keys().asSequence().associateWith { get(it) }
                                }
                            },
                        )
                    )
                }
            }
        } ?: listOf()

    }


}