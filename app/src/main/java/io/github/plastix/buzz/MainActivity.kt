package io.github.plastix.buzz

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.squareup.moshi.Moshi
import okhttp3.*
import java.io.IOException


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val moshi = Moshi.Builder().build()
        val client = OkHttpClient()
        val request = Request.Builder().url("https://www.nytimes.com/puzzles/spelling-bee")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                @Suppress("RegExpRedundantEscape")
                val parser = "gameData = (\\{.*?\\}\\})".toRegex()
                val body = response.body?.string() ?: ""
                val payload = parser.find(body)?.groupValues?.get(1) ?: ""
                val adapter = moshi.adapter(PuzzleContainerResponse::class.java)
                val puzzle = adapter.fromJson(payload)
                println(puzzle)
            }
        })
    }
}
