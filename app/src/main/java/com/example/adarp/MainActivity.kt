package com.example.adarp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val zadania_btn = findViewById<Button>(R.id.zadania_btn)
        val zadania_list_btn = findViewById<Button>(R.id.zadania_list_btn)
        val kopia_btn = findViewById<Button>(R.id.kopia_btn)
        val closed_tasks_btn = findViewById<Button>(R.id.closed_tasks_btn)

        setNumOfTask(zadania_list_btn)

        zadania_list_btn.setOnClickListener{
            val Intent = Intent(this, ViewTasksActivity2::class.java)
            startActivity(Intent)
        }

        zadania_btn.setOnClickListener{
            val Intent = Intent(this, ZadaniaAdd::class.java)
            startActivity(Intent)
        }

        kopia_btn.setOnClickListener{
            val Intent = Intent(this, ViewCopiesActivity::class.java)
            startActivity(Intent)
        }

        closed_tasks_btn.setOnClickListener{
            val Intent = Intent(this, ViewClosedTasksActivity::class.java)
            startActivity(Intent)
        }

    }




    private fun setNumOfTask(btn: Button){
        val queue = Volley.newRequestQueue(this)
        val request = StringRequest(
            Request.Method.GET, EndPoints.URL_GET_TASKS,
            Response.Listener { response ->
                try {
                    val data = response.toString()
                    val jobj = JSONObject(data)
                    val jarray = jobj.getJSONArray("result")
                    val jarraylen = jarray.length()
                    btn.setText(jarraylen.toString())
                    if(jarraylen > 10) {
                        btn.setTextColor(resources.getColor(R.color.full))
    //                    btn.setBackgroundColor(resources.getColor(R.color.full))
                    }
                    if(jarraylen in 7..10) {
                        btn.setTextColor(resources.getColor(R.color.many))
                    }
                    if(jarraylen in 0..6) {
                        btn.setTextColor(resources.getColor(R.color.little))
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Nie udało się pobrać danych", Toast.LENGTH_LONG).show()
                }

            },
            Response.ErrorListener { error ->
                val er = error.toString()
                Log.e("error", er)
            })

        queue.add(request)
    }



}
