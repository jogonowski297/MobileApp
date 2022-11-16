package com.example.adarp

import android.app.ActionBar
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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

class ViewTasksActivity2 : AppCompatActivity() {

    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var recyclerview: RecyclerView

    lateinit var tasksInMemory: SharedPreferences
    lateinit var workersInMemory: SharedPreferences
    lateinit var companyInMemory: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_tasks2)

        tasksInMemory = getSharedPreferences("ALL_TASKS", MODE_PRIVATE)
        workersInMemory = getSharedPreferences("COLORS", MODE_PRIVATE)
        companyInMemory = getSharedPreferences("COMPANY", MODE_PRIVATE)

        getTasks()
        getDataFromWorker(workersInMemory)
        getDataFromCompany(companyInMemory)



        recyclerview = findViewById(R.id.recycleView)
        swipeRefreshLayout = findViewById(R.id.container)

        recyclerview.layoutManager = LinearLayoutManager(this)
        val data = ArrayList<Task>()


        val result = workersInMemory.getString("workers", "XXX")
        val list = Regex("\\w+")
            .findAll(result.toString())
            .toList()
            .map { it.value }   // zwraca [Kuba, Bartek, Krzysiek]


        for (i in 0..tasksInMemory.getInt("taskInMemorySize", 0)) {
            for(j in list){
                if(tasksInMemory.getString("${i}_task_worker", "ERROR").toString() == j){
                    val color = workersInMemory.getString("${j}_color", "#FF00C9")
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


        swipeRefreshLayout.setOnRefreshListener {

            // on below line we are setting is refreshing to false.
            swipeRefreshLayout.isRefreshing = false

            getTasks()

            val intent = Intent(this, ViewTasksActivity2::class.java)
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

        adapter.onBtnMenuClick = { btn ->
            val popupMenu = PopupMenu(this,btn)
            showTaskMenu(popupMenu, adapter, workersInMemory, companyInMemory)
        }


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

    private fun showTaskInBiggerWindow_delete(id_task: String, url: String, worker: String, company: String, subejct: String, date: String, workersInMemory: SharedPreferences, companyInMemory: SharedPreferences, remove: Boolean){
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
            if (remove)
                deleteTask(id_task,url)
            else
                deleteAndAddToEnded(id_task,url,worker,company,subejct,date, workersInMemory, companyInMemory)
            myDialog.dismiss()

            // odswiezenie strony
            val intent = Intent(this, ViewTasksActivity2::class.java)
            startActivity(intent)

        }

        myDialog.show()
    }

    private fun showTaskMenu(popupMenu: PopupMenu, adapter: CustomAdapter2, workersInMemory: SharedPreferences, companyInMemory: SharedPreferences){
        adapter.onItemClick = { Task ->
            popupMenu.menuInflater.inflate(R.menu.popup_menu_task, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.end_task -> {
                        showTaskInBiggerWindow_delete(
                            Task.getIdTask(),
                            EndPoints.URL_ADD_TASKS_CLOSED,
                            Task.getWorkerTask(),
                            Task.getCompanyTask(),
                            Task.getSubjectTask(),
                            Task.getDateTask(),
                            workersInMemory,
                            companyInMemory,
                            false
                        )

                        Toast.makeText(this, "You Clicked : " + item.title, Toast.LENGTH_SHORT)
                            .show()
                    }
                    R.id.edit_task ->
                        Toast.makeText(this, "You Clicked : " + item.title, Toast.LENGTH_SHORT)
                            .show()
                    R.id.delete_task -> {
                        showTaskInBiggerWindow_delete(
                            Task.getIdTask(),
                            EndPoints.URL_DEL_TASK,
                            Task.getWorkerTask(),
                            Task.getCompanyTask(),
                            Task.getSubjectTask(),
                            Task.getDateTask(),
                            workersInMemory,
                            companyInMemory,
                            true
                        )
                            Toast.makeText (this, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()
                    }
                }
                true
            })
            popupMenu.show()
        }
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

                    for (i in 0..array.length() - 1) {
                        val objectArtist = array.getJSONObject(i)
                        editor.putString("${objectArtist.getString("worker")}_color","${objectArtist.getString("color")}")
                        editor.putString("${objectArtist.getString("worker")}_id","${objectArtist.getString("id")}")
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

                    for (i in 0..array.length() - 1) {
                        val objectArtist = array.getJSONObject(i)
                        editor.putString("${objectArtist.getString("company")}_id","${objectArtist.getString("id")}")
                        editor.putString("${objectArtist.getString("id")}_company","${objectArtist.getString("company")}")
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

    private fun getTasks() {
        val stringRequest = StringRequest(
            Request.Method.GET,
            EndPoints.URL_GET_TASKS,
            { s ->
                try {
                    val editor = tasksInMemory.edit()
                    tasksInMemory.edit().clear().apply()
                    val obj = JSONObject(s)
                    val array = obj.getJSONArray("result")

                    editor.putInt("taskInMemorySize", array.length())
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

    private fun deleteTask(task_id_: String, url: String){
        val task_id = task_id_

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
                return params
            }
        }

        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }


    private fun deleteAndAddToEnded(task_id_: String, url: String, worker: String, company: String, sub: String, date_: String, workersInMemory: SharedPreferences, companyInMemory: SharedPreferences) {
        val task_id = task_id_
        val worker_id = workersInMemory.getString("${worker}_id", "ERROR").toString()
        val company_id = companyInMemory.getString("${company}_id", "ERROR").toString()
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