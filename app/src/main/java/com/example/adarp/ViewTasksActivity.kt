package com.example.adarp

import android.app.ActionBar.LayoutParams
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.*
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

        val sharedPreference =  getSharedPreferences("COLORS",Context.MODE_PRIVATE)

        val listView = findViewById<ListView>(R.id.listViewTasks)
        val taskList: MutableList<Task> = mutableListOf()

        getTaskColor(sharedPreference)
        loadArtists(taskList, listView, sharedPreference)

        println("shared: $sharedPreference")

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


    private fun showTaskInBiggerWindow(id_task: String,date: String, worker: String, company: String, subejct: String){
        val dialogBinding = layoutInflater.inflate(R.layout.activity_custom_dialog_task, null)
        val myDialog = Dialog(this)
        myDialog.setContentView(dialogBinding)
        dialogBinding.findViewById<TextView>(R.id.idTask).text = "Zadanie numer ${id_task}"
        dialogBinding.findViewById<TextView>(R.id.dateTask).text = "${date}"
        dialogBinding.findViewById<TextView>(R.id.workerTask).text = "${worker}"
        dialogBinding.findViewById<TextView>(R.id.companyTask).text = "${company}"
        dialogBinding.findViewById<TextView>(R.id.subjectTask).text = "${subejct}"
        myDialog.setCancelable(true)
        myDialog.window?.setBackgroundDrawableResource(R.drawable.round_corner_task)
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        println("width: ${width}")
        if(width <= 1200)
            myDialog.window?.setLayout(width,LayoutParams.WRAP_CONTENT)
        else
            myDialog.window?.setLayout(1200,LayoutParams.WRAP_CONTENT)

        myDialog.show()
    }

    private fun getTaskColor(sharedPreference: SharedPreferences) {
        val stringRequest = StringRequest(Request.Method.GET,
            EndPoints.URL_GET_COLOR_USERS,
            { s ->
                try {
                    val editor = sharedPreference.edit()
                    val obj = JSONObject(s)
                    val array = obj.getJSONArray("result")
                    val workersList: MutableList<String> = mutableListOf()

                    for (i in 0..array.length() - 1) {
                        val objectArtist = array.getJSONObject(i)
                        editor.putString("${objectArtist.getString("worker")}_color","${objectArtist.getString("color")}")
                        editor.putString("${objectArtist.getString("worker")}_id","${objectArtist.getString("id")}")
                        workersList.add(objectArtist.getString("worker"))

                    }

                    editor.putString("workers", workersList.toString())
                    editor.commit()

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { volleyError -> Toast.makeText(this, volleyError.message, Toast.LENGTH_LONG).show() })

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }


    private fun loadArtists(taskList: MutableList<Task>, listView: ListView, sharedPreference: SharedPreferences) {
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

                        val adapter = TaskList(this@ViewTasksActivity, taskList, sharedPreference)

                        listView.adapter = adapter

                    }
                    listView.setOnItemClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long ->
                        showTaskInBiggerWindow(
                            taskList[position].getIdTask(),
                            taskList[position].getDateTask(),
                            taskList[position].getWorkerTask(),
                            taskList[position].getCompanyTask(),
                            taskList[position].getSubjectTask()
                        )
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