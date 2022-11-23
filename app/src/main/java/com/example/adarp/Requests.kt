package com.example.adarp

import android.app.ProgressDialog
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList


//
//
//  DO USUNIECIA
//
//


@Suppress("DEPRECATION")
class Requests : AppCompatActivity()  {
    val sharedPreference =  PreferenceManager.getDefaultSharedPreferences(this)


    fun getCompanys() {
        val stringRequest = StringRequest(Request.Method.GET,
            EndPoints.URL_GET_COMPANY,
            { s ->
                try {
                    val editor = this.sharedPreference.edit()
                    val obj = JSONObject(s)
                    val array = obj.getJSONArray("result")
                    val workersList: MutableList<String> = mutableListOf()

                    for (i in 0..array.length() - 1) {
                        val objectArtist = array.getJSONObject(i)
                        editor.putString("${objectArtist.getString("company")}_id","${objectArtist.getString("id")}")
                        editor.putString("${objectArtist.getString("id")}_company","${objectArtist.getString("company")}")
                        workersList.add(objectArtist.getString("company"))
                    }

                    editor.putString("companies", workersList.toString())
                    editor.apply()
                    println("NOWA KLASA: ${workersList}")

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { volleyError -> Toast.makeText(this, volleyError.message, Toast.LENGTH_LONG).show() })

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }
}