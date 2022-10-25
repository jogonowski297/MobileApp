package com.example.adarp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class ViewTasksActivity : AppCompatActivity() {

//    private var listView: ListView = null
//    private var taskList: MutableList<Task>? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_tasks)

        val listView = findViewById<ListView>(R.id.listViewTasks)
        val taskList: MutableList<Task> = mutableListOf()

        loadArtists(taskList, listView)

        val home_btn = findViewById<Button>(R.id.home_btn)
        home_btn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val add_btn = findViewById<Button>(R.id.add_btn)
        add_btn.setOnClickListener{
            val intent = Intent(this, ZadaniaAdd::class.java)
            startActivity(intent)
        }
    }

    fun refresh(){
        println("REFRESH")
    }

    private fun loadArtists(taskList: MutableList<Task>, listView: ListView) {
        val stringRequest = StringRequest(Request.Method.GET,
            EndPoints.URL_GET_TASKS,
            { s ->
                try {
                    val obj = JSONObject(s)
                    val array = obj.getJSONArray("result")

                    for (i in 0..array.length() - 1) {
                        val objectArtist = array.getJSONObject(i)
                        val task = Task(
                            objectArtist.getString("id"),
                            objectArtist.getString("worker"),
                            objectArtist.getString("company"),
                            objectArtist.getString("subject"),
                            objectArtist.getString("date_add")
                        )
                        taskList.add(task)
                        val adapter = TaskList(this@ViewTasksActivity, taskList)
                        listView.adapter = adapter

                        listView.setOnItemClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long ->
                            Toast.makeText(this@ViewTasksActivity, "JPJP ${position}", Toast.LENGTH_SHORT).show()
                        }

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