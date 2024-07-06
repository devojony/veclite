package com.devo.build_sqlite

import android.content.Context
import com.devo.veclite.VecliteLib
import org.sqlite.database.sqlite.SQLiteDatabase
import org.sqlite.database.sqlite.SQLiteOpenHelper


class AppSQLiteOpenHelper(
    private val context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    private val TAG = "AppSQLiteOpenHelper"

    companion object {
        init {
            System.loadLibrary("sqlite3")
        }
    }

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)

        db?.execSQL("select load_extension('libveclite.so')")
    }
    override fun onCreate(db: SQLiteDatabase?) {

        //创建数据库sql语句并执行
        val useSql = """
            create table if not exists vectors(
            id integer primary key autoincrement,
            content varchar(2000),
            vec text
            )
        """.trimIndent()
        db?.execSQL(useSql)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}