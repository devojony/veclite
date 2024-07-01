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
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random


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
        AppSQLiteOpenHelper(this, "user.db", null, 1).apply {

            writableDatabase?.let { db ->
                (0..5).forEach { _ ->
                    db.insert(
                        "location",
                        null,
                        ContentValues().apply {
                            put("lat", Random.nextDouble(3.0) + 39.904211)
                            put("lng", Random.nextDouble(3.0) + 116.407394)
                        }
                    )
                }
            }

            readableDatabase?.let {
                val cursor = it.rawQuery(
                    "select * from location order by distance(lat, lng, 39.904211, 116.407394) limit 100;",
                    null,
                )
                cursor.moveToFirst()
                while (cursor.moveToNext()) {
                    val id = cursor.getInt(0)
                    val lat = cursor.getDouble(1)
                    val lng = cursor.getDouble(2)
                    Log.d("TAG", "onStart: $id $lat $lng")
                }

            }

        }
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier,)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Build_sqliteTheme {
        Greeting("Android")
    }
}
