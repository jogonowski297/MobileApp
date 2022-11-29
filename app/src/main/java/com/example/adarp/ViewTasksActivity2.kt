package com.example.adarp

import android.app.ActionBar
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
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

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerview: RecyclerView

    private var SP: MySharedPreference = MySharedPreference()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_tasks2)


        SP.tasksInMemory = getSharedPreferences("ALL_TASKS", MODE_PRIVATE)
        SP.workersInMemory = getSharedPreferences("COLORS", MODE_PRIVATE)
        SP.companyInMemory = getSharedPreferences("COMPANY", MODE_PRIVATE)

        getTasks()
        getDataFromWorker(SP.workersInMemory)
        getDataFromCompany(SP.companyInMemory)



        recyclerview = findViewById(R.id.recycleView)
        swipeRefreshLayout = findViewById(R.id.container)

        recyclerview.layoutManager = LinearLayoutManager(this)
        val data = ArrayList<Task>()


        val result = SP.workersInMemory.getString("workers", "XXX")
        val list = Regex("\\w+")
            .findAll(result.toString())
            .toList()
            .map { it.value }   // zwraca [Kuba, Bartek, Krzysiek]


        for (i in 0..SP.tasksInMemory.getInt("taskInMemorySize", 0)) {
            for(j in list){
                if(SP.tasksInMemory.getString("${i}_task_worker", "ERROR").toString() == j){
                    val color = SP.workersInMemory.getString("${j}_color", "#FF00C9")
                    data.add(Task(
                        SP.tasksInMemory.getString("${i}_task_id", "ERROR").toString(),
                        SP.tasksInMemory.getString("${i}_task_worker", "ERROR").toString(),
                        SP.tasksInMemory.getString("${i}_task_company", "ERROR").toString(),
                        SP.tasksInMemory.getString("${i}_task_subject", "ERROR").toString(),
                        SP.tasksInMemory.getString("${i}_task_date_add", "ERROR").toString(),
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
            showTaskMenu(popupMenu, adapter, SP.workersInMemory, SP.companyInMemory)
        }


        val btn_back = findViewById<Button>(R.id.btn_back)
        btn_back.setOnClickListener{
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
                    R.id.edit_task -> {
                        val v = LayoutInflater.from(this).inflate(R.layout.activity_edit_task, null)
                        val subject: EditText = v.findViewById(R.id.editSubject)
                        subject.setText(Task.getSubjectTask())
                        val ETspinnerWorkers: Spinner = v.findViewById(R.id.edit_task_spinnerWorker)
                        val ETspinnerCompany: Spinner = v.findViewById(R.id.edit_task_spinnerCompany)



                        loadToSpinner(workersInMemory, ETspinnerWorkers, "worker", Task.getWorkerTask())
                        loadToSpinner(companyInMemory, ETspinnerCompany, "company", Task.getCompanyTask())

                        AlertDialog.Builder(this)
                            .setView(v)
                            .setPositiveButton("Ok"){
                                dialog,_->
                                val worker_name: Model = ETspinnerWorkers.selectedItem as Model
                                val company_name: Model = ETspinnerCompany.selectedItem as Model

                                editTask(Task.getIdTask(), worker_name.name, company_name.name, subject.text.toString())
                                Toast.makeText(this, "Zmienione", Toast.LENGTH_LONG).show()
                            }

                            .setNegativeButton("Wyjdź"){
                                dialog,_->
                                Toast.makeText(this, "exit", Toast.LENGTH_LONG).show()
                            }
                            .create().show()

                        Toast.makeText(this, "You Clicked : " + item.title, Toast.LENGTH_SHORT)
                            .show()
                    }
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

    private fun showMenu(popupMenu: PopupMenu){
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.add_task -> {
                    val intent = Intent(this, ZadaniaAdd::class.java)
                    startActivity(intent)

                }
                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                }
                R.id.closed_task -> {
                    val intent = Intent(this, ViewClosedTasksActivity::class.java)
                    startActivity(intent)

                }
            }
            true
        })
        popupMenu.show()

    }

    private fun loadToSpinner(somethingInMemory: SharedPreferences, spinner: Spinner, nazwa: String, firstPosition: String = "") {
        val list: ArrayList<Model> = ArrayList()

        for (i in 1..somethingInMemory.getInt("somethingInMemorySize", 0)) {
            val id = somethingInMemory.getInt("${i}_id", 999)
            val name = somethingInMemory.getString("${i}_${nazwa}", "ERROR").toString()
            val m = Model(id, name)
            list.add(m)
        }
        list.sortBy { model: Model -> model.name }
        list.add(0, Model(0, firstPosition))
        val adapter = CustomAdapter(this, list)
        println("adapter: ${list}")
        spinner.adapter = adapter
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

    private fun getTasks() {
        val stringRequest = StringRequest(
            Request.Method.GET,
            EndPoints.URL_GET_TASKS,
            { s ->
                try {
                    val editor = SP.tasksInMemory.edit()
                    SP.tasksInMemory.edit().clear().apply()
                    val obj = JSONObject(s)
                    val array = obj.getJSONArray("result")

                    editor.putInt("taskInMemorySize", array.length())
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

    private fun editTask(task_id: String, worker: String, company: String, subejct: String){

        val worker_id = SP.workersInMemory.getInt("${worker}_id", 0)
        val company_id = SP.companyInMemory.getInt("${company}_id", 0)

        val stringRequest = object : StringRequest(
            Method.POST, EndPoints.URL_EDIT_TASK,
            Response.Listener { response ->
                try {
                    val obj = JSONObject(response)
                    Toast.makeText(this@ViewTasksActivity2, obj.getString("message"), Toast.LENGTH_LONG).show()

                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this@ViewTasksActivity2, "2", Toast.LENGTH_LONG).show()
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
                params.put("worker_id", "$worker_id")
                params.put("company_id", "$company_id")
                params.put("subject", subejct)
                return params
            }
        }

        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }


    private fun deleteAndAddToEnded(task_id_: String, url: String, worker: String, company: String, sub: String, date_: String, workersInMemory: SharedPreferences, companyInMemory: SharedPreferences) {
        val task_id = task_id_
        val worker_id = workersInMemory.getInt("${worker}_id", 0)
        val company_id = companyInMemory.getInt("${company}_id", 0)
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
                params.put("worker_id", "$worker_id")
                params.put("company_id", "$company_id")
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