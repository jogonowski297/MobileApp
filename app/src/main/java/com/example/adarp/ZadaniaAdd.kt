package com.example.adarp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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


class ZadaniaAdd : AppCompatActivity() {

    //edittext and spinner
    private var editTextArtistName: EditText? = null
    private var spinnerWorker: Spinner? = null
    private var spinnerCompany: Spinner? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zadania_add)

        editTextArtistName = findViewById(R.id.editTextArtistName) as EditText
        spinnerWorker = findViewById(R.id.spinnerWorker) as Spinner
        spinnerCompany= findViewById(R.id.spinnerCompany) as Spinner

        downloadTask(EndPoints.URL_GET_COMPANY,"result", "company", spinnerCompany!!)
        downloadTask(EndPoints.URL_GET_WORKERS,"result", "worker", spinnerWorker!!)



        val buttonaddart = findViewById<Button>(R.id.buttonAddArtist)
        buttonaddart.setOnClickListener {
            getLenTasksAndAddTask(EndPoints.URL_GET_TASKS)
        }


        val buttonviewart = findViewById<Button>(R.id.buttonViewArtist)
        buttonviewart.setOnClickListener {
            val intent = Intent(this, ViewTasksActivity::class.java)
            startActivity(intent)
        }

        val return_btn = findViewById<Button>(R.id.add_btn)
        return_btn.setOnClickListener {
            val Intent = Intent(this, MainActivity::class.java)
            startActivity(Intent)
        }
    }

    private fun getDateTime(): String {
        val sdf = SimpleDateFormat("yyyy/M/dd hh:mm:ss")
        val currentDate = sdf.format(Date())
        println("DATE: $currentDate")
        return currentDate
    }

    private fun downloadTask(url: String, arr_name: String, key: String, spi: Spinner) {
        val pd = ProgressDialog(this)
        pd.setMessage("downloading...")
        pd.show()
        val queue = Volley.newRequestQueue(this)
        val request = StringRequest(Request.Method.GET, url,
            Response.Listener { response ->
                pd.dismiss()
                val data = response.toString()
                val jobj = JSONObject(data)
                val jarray = jobj.getJSONArray(arr_name)
                val list: MutableList<Model> = ArrayList()
                for(i in 0..jarray.length()-1) {
                    val jobj2 = jarray.getJSONObject(i)
                    val id = jobj2.getString("id")
                    val name = jobj2.getString(key)
                    val m = Model(id.toInt(),name)
                    list.add(m)
                }
                val adapter = CustomAdapter(this, list)
                println("adapter: $adapter")
                spi.adapter = adapter

            },
            Response.ErrorListener { error ->
                pd.dismiss()
                val er = error.toString()
                Log.e("error", er)
            })
        queue.add(request)
    }

    fun getLenTasksAndAddTask(url:String){
        val queue = Volley.newRequestQueue(this)
        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                    val data = response.toString()
                    val jobj = JSONObject(data)
                    val jarray = jobj.getJSONArray("result")
                    val jarraylen = jarray.length()
                    val request1 = StringRequest(
                        Request.Method.GET, EndPoints.URL_GET_TASKS_CLOSED,
                        Response.Listener { response ->
                            try {
                                val data1 = response.toString()
                                val jobj1 = JSONObject(data1)
                                val jarray1 = jobj1.getJSONArray("result")
                                val jarraylen1 = jarray1.length()
                                addArtist(jarraylen1.toInt() + jarraylen.toInt() + 1)
                            }catch (e: JSONException) {
                                addArtist(1)
                            }

                        },
                        Response.ErrorListener { error ->
                            val er = error.toString()
                            Log.e("error", er)
                        })
                queue.add(request1)

            },
            { error ->
                val er = error.toString()
                Log.e("error", er)
            })

        queue.add(request)
    }

    //adding a new record to database
    private fun addArtist(len: Int) {
        //getting the record values
        val id = len
        val worker_id = spinnerWorker?.selectedItem.toString()
        val company_id = spinnerCompany?.selectedItem.toString()
        val subject = editTextArtistName?.text.toString()
        val date = getDateTime()
        //creating volley string request
        val stringRequest = object : StringRequest(
            Method.POST, EndPoints.URL_ADD_ARTIST,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    Toast.makeText(applicationContext, obj.getString("message"), Toast.LENGTH_LONG)
                        .show()
                } catch (e: JSONException) {
                    e.printStackTrace()
                    println("cos:" + e.printStackTrace())
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(volleyError: VolleyError) {
                    Toast.makeText(applicationContext, volleyError.message, Toast.LENGTH_LONG)
                        .show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("id", id.toString())
                params.put("worker_id", worker_id.slice(9..9))
                params.put("company_id", company_id.slice(9..9))
                params.put("subject", subject)
                params.put("date_add", date)
                println("params: $params")
                return params
            }
        }

        //adding request to queue
        VolleySingleton.instance?.addToRequestQueue(stringRequest)

        val intent = Intent(this, ViewTasksActivity::class.java)
        finish()
        startActivity(intent)
    }



}


