package com.example.adarp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class ViewCopiesActivity : AppCompatActivity() {

//    private var listView: ListView? = null
//    private var copyList: MutableList<Copies>? = null

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerview: RecyclerView

    private var SP: MySharedPreference = MySharedPreference()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_copies)


        SP.copiesInMemory = getSharedPreferences("ALL_COPIES", MODE_PRIVATE)

        getDataFromCopies(SP.copiesInMemory)


        recyclerview = findViewById(R.id.recycleView)
        swipeRefreshLayout = findViewById(R.id.container)

        recyclerview.layoutManager = LinearLayoutManager(this)
        val data = ArrayList<Copies>()

        for (i in 0..SP.copiesInMemory.getInt("somethingInMemorySize", 0)-1) {
            var color = "#11B600"
            if (SP.copiesInMemory.getString("${i}_kopia", "FALSE").toString() == "FALSE") {
                color = "#D80000"
            }

            data.add(
                Copies(
                    SP.copiesInMemory.getInt("${i}_id", 0),
                    SP.copiesInMemory.getString("${i}_nazwa", "ERROR").toString(),
                    SP.copiesInMemory.getString("${i}_rozmiar", "ERROR").toString(),
                    SP.copiesInMemory.getString("${i}_data_", "ERROR").toString(),
                    SP.copiesInMemory.getString("${i}_kopia", "ERROR").toString(),
                    color
                )
            )
        }

        val adapter = CustomAdapterCopies(data)
        recyclerview.adapter = adapter

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false

            getDataFromCopies(SP.copiesInMemory)

            val intent = Intent(this, ViewCopiesActivity::class.java)
            startActivity(intent)
        }

        val btn_back = findViewById<Button>(R.id.btn_back)
        btn_back.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }


    private fun getDataFromCopies(sharedPreference: SharedPreferences) {
        val stringRequest = StringRequest(Request.Method.GET,
            EndPoints.URL_GET_COPIES,
            { s ->
                try {
                    val editor = sharedPreference.edit()
                    val obj = JSONObject(s)
                    val array = obj.getJSONArray("result")
                    editor.putInt("somethingInMemorySize", array.length())
                    for (i in 0..array.length() - 1) {
                        val objectArtist = array.getJSONObject(i)
                        editor.putInt("${i}_id", objectArtist.getInt("id"))
                        editor.putString("${i}_nazwa", objectArtist.getString("nazwa"))
                        editor.putString("${i}_rozmiar","${objectArtist.getString("rozmiar")}")
                        editor.putString("${i}_data_", "${objectArtist.getString("data_")}")
                        editor.putString("${i}_kopia", objectArtist.getString("kopia"))
                    }


                    editor.apply()

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { volleyError -> Toast.makeText(this, volleyError.message, Toast.LENGTH_LONG).show() })

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

}