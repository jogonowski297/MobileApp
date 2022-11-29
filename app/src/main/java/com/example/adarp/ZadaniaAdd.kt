package com.example.adarp

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
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

    private var editTextArtistName: EditText? = null
    private var spinnerWorker: Spinner? = null
    private var spinnerCompany: Spinner? = null

    lateinit var workersInMemory: SharedPreferences
    lateinit var companyInMemory: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zadania_add)

        editTextArtistName = findViewById(R.id.editTextArtistName) as EditText
        spinnerWorker = findViewById(R.id.spinnerWorker) as Spinner
        spinnerCompany= findViewById(R.id.spinnerCompany) as Spinner

        workersInMemory = getSharedPreferences("WORKER", MODE_PRIVATE)
        companyInMemory = getSharedPreferences("COMPANY", MODE_PRIVATE)

        downloadDataToSpinner(EndPoints.URL_GET_COMPANY,"result", "company", companyInMemory)
        downloadDataToSpinner(EndPoints.URL_GET_WORKERS,"result", "worker", workersInMemory)

        loadToSpinner(companyInMemory, spinnerCompany!!)
        loadToSpinner(workersInMemory, spinnerWorker!!)



        val buttonaddart = findViewById<Button>(R.id.buttonAddArtist)
        buttonaddart.setOnClickListener {
            addArtist()
            val intent = Intent(this, MainActivity::class.java)
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

    private fun loadToSpinner(somethingInMemory: SharedPreferences, spinner: Spinner){
        val list: ArrayList<Model> = ArrayList()
        println("Dlugosc spinnera: ${somethingInMemory.getInt("somethingInMemorySize", 0)}")
        for(i in 1..somethingInMemory.getInt("somethingInMemorySize", 0)) {
            val id = somethingInMemory.getInt("${i}_int", 0)
            val name = somethingInMemory.getString("${i}_string", "ERROR").toString()
            val m = Model(id, name)
            list.add(m)
        }
        list.sortBy { model: Model -> model.name }
        val adapter = CustomAdapter(this, list)
        println("adapter: ${list}")
        spinner.adapter = adapter
    }

    private fun downloadDataToSpinner(url: String, arr_name: String, key: String, somethingInMemory: SharedPreferences) {
        val pd = ProgressDialog(this)
        pd.setMessage("downloading...")
        pd.show()
        val queue = Volley.newRequestQueue(this)
        val request = StringRequest(Request.Method.GET, url,
            Response.Listener { response ->
                pd.dismiss()
                val editor = somethingInMemory.edit()
                val data = response.toString()
                val jobj = JSONObject(data)
                val jarray = jobj.getJSONArray(arr_name)
                val list: MutableList<Model> = ArrayList()
                editor.putInt("somethingInMemorySize", jarray.length())
                for(i in 0..jarray.length()-1) {
                    val jobj2 = jarray.getJSONObject(i)
                    println("posortowane: ${jobj2.getString(key)}")
                    editor.putString("${jobj2.getString("id")}_string", jobj2.getString(key))
                    editor.putInt("${jobj2.getString("id")}_int", jobj2.getString("id").toInt())
                }
                editor.apply()
            },
            Response.ErrorListener { error ->
                pd.dismiss()
                val er = error.toString()
                Log.e("error", er)
            })
        queue.add(request)
    }


    private fun addArtist() {
        //getting the record values
        val worker_id = spinnerWorker?.selectedItem.toString()
        val company_id = spinnerCompany?.selectedItem.toString()
        val subject = editTextArtistName?.text.toString()
        val date = getDateTime()

        val stringRequest = object : StringRequest(
            Method.POST, EndPoints.URL_ADD_ARTIST,
            Response.Listener{ response ->
                try {
                    val obj = JSONObject(response)
                    Toast.makeText(applicationContext, obj.getString("message"), Toast.LENGTH_LONG)
                        .show()
                } catch (e: JSONException) {
                    e.printStackTrace()
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
                params.put("worker_id", worker_id.slice(9..9))
                params.put("company_id", company_id.slice(9..9))
                params.put("subject", subject)
                params.put("date_add", date)
                return params
            }
        }
        //adding request to queue

        VolleySingleton.instance?.addToRequestQueue(stringRequest)


    }



}


