package com.example.networkapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var numberEditText: EditText
    private lateinit var showButton: Button
    private lateinit var comicImageView: ImageView

    private val fileName = "savedComic.txt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestQueue = Volley.newRequestQueue(this)

        titleTextView = findViewById(R.id.comicTitleTextView)
        descriptionTextView = findViewById(R.id.comicDescriptionTextView)
        numberEditText = findViewById(R.id.comicNumberEditText)
        showButton = findViewById(R.id.showComicButton)
        comicImageView = findViewById(R.id.comicImageView)

        showButton.setOnClickListener {
            val comicId = numberEditText.text.toString()
            if (comicId.isNotEmpty()) {
                downloadComic(comicId)
            } else {
                Toast.makeText(this, "Please enter a comic number", Toast.LENGTH_SHORT).show()
            }
        }

        loadSavedComicInfo()
    }

    private fun downloadComic(comicId: String) {
        val url = "https://xkcd.com/$comicId/info.0.json"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response -> showComic(response) },
            { error -> Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show() }
        )
        requestQueue.add(jsonObjectRequest)
    }

    private fun showComic(comicObject: JSONObject) {
        val title = comicObject.getString("title")
        val description = comicObject.getString("alt")
        val imageUrl = comicObject.getString("img")

        titleTextView.text = title
        descriptionTextView.text = description
        Picasso.get().load(imageUrl).into(comicImageView)

        saveComicInfo(comicObject.getString("num"), title, description, imageUrl)
    }

    private fun saveComicInfo(comicId: String, title: String, description: String, imageUrl: String) {
        val fileContent = "$comicId|$title|$description|$imageUrl"
        openFileOutput(fileName, MODE_PRIVATE).use { outputStream ->
            outputStream.write(fileContent.toByteArray())
        }
    }

    private fun loadSavedComicInfo() {
        val file = File(filesDir, fileName)
        if (file.exists()) {
            val content = FileInputStream(file).bufferedReader().use { it.readText() }
            val (comicId, title, description, imageUrl) = content.split('|')
            titleTextView.text = title
            descriptionTextView.text = description
            Picasso.get().load(imageUrl).into(comicImageView)
            numberEditText.setText(comicId)
        }
    }
}
