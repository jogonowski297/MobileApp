package com.example.adarp

import android.app.ActionBar
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class ViewClosedTasksActivity : AppCompatActivity() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerview: RecyclerView

    private var SP: MySharedPreference = MySharedPreference()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_closed_tasks)

        SP.closedTasksInMemory = getSharedPreferences("ALL_CLOSED_TASKS", MODE_PRIVATE)
        SP.workersInMemory = getSharedPreferences("COLORS", MODE_PRIVATE)
        SP.companyInMemory = getSharedPreferences("COMPANY", MODE_PRIVATE)

        getClosedTasks()
        getDataFromWorker(SP.workersInMemory)
        getDataFromCompany(SP.companyInMemory)

        recyclerview = findViewById(R.id.recycleView_closedTask)
        swipeRefreshLayout = findViewById(R.id.container_closedTask)

        recyclerview.layoutManager = LinearLayoutManager(this)
        val data = ArrayList<Task>()


        val result = SP.workersInMemory.getString("workers", "XXX")
        val list = Regex("\\w+")
            .findAll(result.toString())
            .toList()
            .map { it.value }   // zwraca [Kuba, Bartek, Krzysiek]


        for (i in 0..SP.closedTasksInMemory.getInt("closedTaskInMemorySize", 0)) {
            for(j in list){
                if(SP.closedTasksInMemory.getString("${i}_task_worker", "ERROR").toString() == j){
                    val color = SP.workersInMemory.getString("${j}_color", "#FF00C9")
                    data.add(Task(
                        SP.closedTasksInMemory.getString("${i}_task_id", "ERROR").toString(),
                        SP.closedTasksInMemory.getString("${i}_task_worker", "ERROR").toString(),
                        SP.closedTasksInMemory.getString("${i}_task_company", "ERROR").toString(),
                        SP.closedTasksInMemory.getString("${i}_task_subject", "ERROR").toString(),
                        SP.closedTasksInMemory.getString("${i}_task_date_add", "ERROR").toString(),
                        color.toString()
                    ))
                }
            }
        }

        val adapter = CustomAdapterClosedTasks(data)
        recyclerview.adapter = adapter


        swipeRefreshLayout.setOnRefreshListener {

            // on below line we are setting is refreshing to false.
            swipeRefreshLayout.isRefreshing = false

            getClosedTasks()

            val intent = Intent(this, ViewClosedTasksActivity::class.java)
            startActivity(intent)


        }


        //    Po kliknieciu na zadanie zostaje wyswielone w większym oknie dla lepszego widoku
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

        val home_btn = findViewById<Button>(R.id.home_btn_closedTask)
        home_btn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
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
//        myDialog.setCancelable(true)
        val animSlideUp: Animation = AnimationUtils.loadAnimation(applicationContext, R.anim.slideup)
        myDialog.window!!.attributes
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        if(width <= 1200)
            myDialog.window?.setLayout(width, ActionBar.LayoutParams.WRAP_CONTENT)
        else
            myDialog.window?.setLayout(1200, ActionBar.LayoutParams.WRAP_CONTENT)

        myDialog.show()
    }


    private fun getDataFromWorker(sharedPreference: SharedPreferences) {
        val stringRequest = StringRequest(Request.Method.GET,
            EndPoints.URL_GET_COLOR_USERS,
            { s ->
                try {
                    val editor = sharedPreference.edit()
                    val obj = JSONObject(s)
                    val array = obj.getJSONArray("result")
                    val workersList: MutableList<String> = mutableListOf()
                    editor.putInt("somethingInMemorySize", array.length())
                    for (i in 0..array.length() - 1) {
                        val objectArtist = array.getJSONObject(i)
                        editor.putString("${objectArtist.getString("worker")}_color","${objectArtist.getString("color")}")
                        editor.putInt("${objectArtist.getString("worker")}_id", objectArtist.getInt("id"))
                        editor.putString("${objectArtist.getString("id")}_worker","${objectArtist.getString("worker")}")
                        editor.putInt("${objectArtist.getString("id")}_id", objectArtist.getInt("id"))
                        workersList.add(objectArtist.getString("worker"))
                    }

                    editor.putString("workers", workersList.toString())
                    editor.apply()

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { volleyError -> Toast.makeText(this, volleyError.message, Toast.LENGTH_LONG).show() })

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    private fun getDataFromCompany(sharedPreference: SharedPreferences) {
        val stringRequest = StringRequest(Request.Method.GET,
            EndPoints.URL_GET_COMPANY,
            { s ->
                try {
                    val editor = sharedPreference.edit()
                    val obj = JSONObject(s)
                    val array = obj.getJSONArray("result")
                    val workersList: MutableList<String> = mutableListOf()
                    editor.putInt("somethingInMemorySize", array.length())
                    for (i in 0..array.length() - 1) {
                        val objectArtist = array.getJSONObject(i)
                        editor.putInt("${objectArtist.getString("company")}_id", objectArtist.getInt("id"))
                        editor.putString("${objectArtist.getString("id")}_company","${objectArtist.getString("company")}")
                        editor.putInt("${objectArtist.getString("id")}_id", objectArtist.getInt("id"))
                        workersList.add(objectArtist.getString("company"))

                    }

                    editor.putString("company", workersList.toString())
                    editor.apply()

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { volleyError -> Toast.makeText(this, volleyError.message, Toast.LENGTH_LONG).show() })

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    private fun getClosedTasks() {
        val stringRequest = StringRequest(
            Request.Method.GET,
            EndPoints.URL_GET_CLOSED_TASKS,
            { s ->
                try {
                    val editor = SP.closedTasksInMemory.edit()
                    SP.closedTasksInMemory.edit().clear().apply()
                    val obj = JSONObject(s)
                    val array = obj.getJSONArray("result")

                    editor.putInt("closedTaskInMemorySize", array.length())
                    for (i in 0..array.length() - 1) {
                        val objectArtist = array.getJSONObject(i)
                        editor.putString("${i}_task_id", "${objectArtist.getString("id")}")
                        editor.putString(
                            "${i}_task_worker",
                            "${objectArtist.getString("worker")}"
                        )
                        editor.putString(
                            "${i}_task_company",
                            "${objectArtist.getString("company")}"
                        )
                        editor.putString(
                            "${i}_task_subject",
                            "${objectArtist.getString("subject")}"
                        )
                        editor.putString(
                            "${i}_task_date_add",
                            "${objectArtist.getString("date_add")}"
                        )
                        editor.putString(
                            "${i}_closed_task_date_add",
                            "${objectArtist.getString("date_close")}"
                        )
                    }
                    editor.apply()

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { volleyError ->
                Toast.makeText(this, volleyError.message, Toast.LENGTH_LONG).show()
            })

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

}