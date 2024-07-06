package com.devo.build_sqlite

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.devo.build_sqlite.ui.theme.Build_sqliteTheme
import com.devo.veclite.VecliteLib
import org.sqlite.database.sqlite.SQLiteDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        VecliteLib()
        setContent {
            Build_sqliteTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting("Android")
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val sep = ","
        val dimen = 1000
        AppSQLiteOpenHelper(this, "vectors.db", null, 1).apply {

            writableDatabase?.let { db ->
                // clean this table
                (0 until 1_00).forEach { id ->
                    val vec = buildList {
                        repeat(dimen){
                            add(id+1.0)
                        }
                    }.joinToString(sep)

                    db.insertWithOnConflict(
                        "vectors",
                        null,
                        ContentValues().apply {
                            put("id", id)
                            put("content", "Llamas are members of the camelid family")
                            put("vec", vec)
                        },
                        SQLiteDatabase.CONFLICT_IGNORE
                    )
                }
            }

            Log.i("TAG", "start query")
            // use sqlite extension
            readableDatabase?.let {

                val qVec = buildList {
                    repeat(dimen){
                        add(10.0)
                    }
                }.joinToString(sep)

                Log.i("TAG", "query vec: $qVec")

                val cursor = it.rawQuery(
                    "select * from vectors order by l2d(vec, ?) limit 3;",
                    arrayOf(qVec)
                )

                Log.i("TAG", "end query")
                while (cursor.moveToNext()) {
                    val id = cursor.getInt(0)
                    val content = cursor.getString(1)
                    val vec = cursor.getString(2)
                    Log.i("TAG", "use sqlite: $id '$content' $vec")
                }


            }

        }.close()
    }

}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Build_sqliteTheme {
        Greeting("Android")
    }
}
