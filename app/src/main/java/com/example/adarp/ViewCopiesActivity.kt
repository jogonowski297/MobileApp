package com.example.adarp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class ViewCopiesActivity : AppCompatActivity() {

    private var listView: ListView? = null
    private var copyList: MutableList<Copies>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_copies)

        listView = findViewById(R.id.listViewCopies)
        copyList = mutableListOf()

        loadArtists()

        val home_btn = findViewById<Button>(R.id.home_btn)
        home_btn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }


    private fun loadArtists() {
        val stringRequest = StringRequest(Request.Method.GET,
            EndPoints.URL_GET_COPIES,
            { s ->
                try {
                    val obj = JSONObject(s)
                    val array = obj.getJSONArray("result")

                    for (i in 0..array.length() - 1) {
                        val objectArtist = array.getJSONObject(i)
                        val copies = Copies(
                            objectArtist.getString("id"),
                            objectArtist.getString("nazwa"),
                            objectArtist.getString("rozmiar"),
                            objectArtist.getString("data_"),
                            objectArtist.getString("kopia")
                        )
                        copyList!!.add(copies)
                        val adapter = CopiesList(this@ViewCopiesActivity, copyList!!)
                        listView!!.adapter = adapter
                        println("adapter:${adapter}")
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { volleyError -> Toast.makeText(applicationContext, volleyError.message, Toast.LENGTH_LONG).show() })

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add<String>(stringRequest)
    }

}