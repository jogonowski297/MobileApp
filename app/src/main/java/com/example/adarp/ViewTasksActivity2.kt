package com.example.adarp

import android.app.ActionBar
import android.app.Dialog
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ViewTasksActivity2 : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_tasks2)
        val tasksInMemory = getSharedPreferences("ALL_TASKS", MODE_PRIVATE)
        val colorInMemory = getSharedPreferences("COLORS", MODE_PRIVATE)

        getTasks(tasksInMemory)
        getTaskColor(colorInMemory)



        val recyclerview = findViewById<RecyclerView>(R.id.recycleView)
        recyclerview.layoutManager = LinearLayoutManager(this)
        val data = ArrayList<Task>()


        val result = colorInMemory.getString("workers", "XXX")
        val list = Regex("\\w+")
            .findAll(result.toString())
            .toList()
            .map { it.value }   // zwraca [Kuba, Bartek, Krzysiek]





        for (i in 0..tasksInMemory.getInt("taskInMemorySize", 0)) {
            for(j in list){
                if(tasksInMemory.getString("${i}_task_worker", "ERROR").toString() == j){
                    val color = colorInMemory.getString("${j}_color", "#FF00C9")
                    data.add(Task(
                        tasksInMemory.getString("${i}_task_id", "ERROR").toString(),
                        tasksInMemory.getString("${i}_task_worker", "ERROR").toString(),
                        tasksInMemory.getString("${i}_task_company", "ERROR").toString(),
                        tasksInMemory.getString("${i}_task_subject", "ERROR").toString(),
                        tasksInMemory.getString("${i}_task_date_add", "ERROR").toString(),
                        color.toString()
                    ))
                }
            }

        }
        val adapter = CustomAdapter2(data)
        recyclerview.adapter = adapter


        recyclerview.layoutManager = LinearLayoutManager(this)
        adapter.onItemClick = { Task ->
            showTaskInBiggerWindow(
                Task.getIdTask(),
                Task.getWorkerTask(),
                Task.getCompanyTask(),
                Task.getSubjectTask(),
                Task.getDateTask()
            )
        }

        val btn_end = recyclerview.findViewById<Button>(R.id.btn_end)

        btn_end.setOnClickListener {
            println("TeRAZ tu: ${recyclerview.findViewById<TextView>(R.id.textViewWorker)}")
            showTaskInBiggerWindow_delete(
                recyclerview.findViewById<TextView>(R.id.id_task).toString(),
                EndPoints.URL_ADD_TASKS_CLOSED,
                recyclerview.findViewById<TextView>(R.id.textViewWorker).toString(),
                recyclerview.findViewById<TextView>(R.id.textViewCompany).toString(),
                recyclerview.findViewById<TextView>(R.id.textViewSubject).toString(),
                recyclerview.findViewById<TextView>(R.id.textViewDate).toString(),
                colorInMemory

            )
        }

    }


    private fun showTaskInBiggerWindow(id_task: String, worker: String, company: String, subejct: String, date: String){
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
            myDialog.window?.setLayout(width, ActionBar.LayoutParams.WRAP_CONTENT)
        else
            myDialog.window?.setLayout(1200, ActionBar.LayoutParams.WRAP_CONTENT)

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

    private fun getTasks(tasksInMemory: SharedPreferences) {
        val stringRequest = StringRequest(
            Request.Method.GET,
            EndPoints.URL_GET_TASKS,
            { s ->
                try {
                    val editor = tasksInMemory.edit()
                    val obj = JSONObject(s)
                    val array = obj.getJSONArray("result")

                    editor.putInt("taskInMemorySize", array.length() - 1)
                    for (i in 0..array.length() - 1) {
                        val objectArtist = array.getJSONObject(i)
                        editor.putString("${i}_task_id","${objectArtist.getString("id")}")
                        editor.putString("${i}_task_worker","${objectArtist.getString("worker")}")
                        editor.putString("${i}_task_company","${objectArtist.getString("company")}")
                        editor.putString("${i}_task_subject","${objectArtist.getString("subject")}")
                        editor.putString("${i}_task_date_add","${objectArtist.getString("date_add")}")
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

    private fun getDateTime(): String {
        val sdf = SimpleDateFormat("yyyy/M/dd hh:mm:ss")
        val currentDate = sdf.format(Date())
        return currentDate
    }

    private fun showTaskInBiggerWindow_delete(id_task: String, url: String, worker: String, company: String, subejct: String,date: String, colorInMemory: SharedPreferences){
        val dialogBinding = this.layoutInflater.inflate(R.layout.activity_custom_dialog_delete, null)
        val myDialog = Dialog(this)
        myDialog.setContentView(dialogBinding)
        dialogBinding.findViewById<TextView>(R.id.idTask).text = "Zadanie numer ${id_task}"
        dialogBinding.findViewById<TextView>(R.id.dateTask).text = "${date}"
        dialogBinding.findViewById<TextView>(R.id.workerTask).text = "${worker}"
        dialogBinding.findViewById<TextView>(R.id.companyTask).text = "${company}"
        dialogBinding.findViewById<TextView>(R.id.subjectTask).text = "${subejct}"

        myDialog.setCancelable(true)
        myDialog.window?.setBackgroundDrawableResource(R.drawable.round_corner_delete)

        val width = (this.resources.displayMetrics.widthPixels * 0.90).toInt()

        if(width <= 1200)
            myDialog.window?.setLayout(width, ActionBar.LayoutParams.WRAP_CONTENT)
        else
            myDialog.window?.setLayout(1200, ActionBar.LayoutParams.WRAP_CONTENT)

        val noBtn = dialogBinding.findViewById<Button>(R.id.noBtn)
        val yesBtn = dialogBinding.findViewById<Button>(R.id.yesBtn)

        noBtn.setOnClickListener {
            myDialog.dismiss()
        }

        yesBtn.setOnClickListener {
            deleteAndAddToEnded(id_task,url,worker,company,subejct,date, colorInMemory)
            myDialog.dismiss()

        }

        myDialog.show()
    }

    private fun deleteAndAddToEnded(task_id_: String, url: String, worker: String, company: String, sub: String, date_: String, colorInMemory: SharedPreferences) {
        val task_id = task_id_
        val worker_id = colorInMemory.getString("${worker}_id", "XXX").toString()
        val company_id = company
        val subject = sub
        val date_add = date_
        val date_close = getDateTime()

        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    val obj = JSONObject(response)
                    Toast.makeText(this@ViewTasksActivity2, obj.getString("message"), Toast.LENGTH_LONG).show()

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(volleyError: VolleyError) {
                    Toast.makeText(this@ViewTasksActivity2, volleyError.message, Toast.LENGTH_LONG)
                        .show()
                }
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("task_id", task_id)
                params.put("worker_id", worker_id)
                params.put("company_id", company_id)
                params.put("subject", subject)
                params.put("date_add", date_add)
                params.put("date_close", date_close)
                println("params: $params")
                return params
            }
        }

        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

}