package com.devo.build_sqlite

import android.content.Context
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
            create table user(
            id integer primary key autoincrement,
            username varchar(20),
            password varchar(20),
            age integer
            )
        """.trimIndent()
        db?.execSQL(useSql)

        val locationSql = """
            create table location(
            id integer primary key autoincrement,
            lat double,
            lng double
            )
        """.trimIndent()
        db?.execSQL(locationSql)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}