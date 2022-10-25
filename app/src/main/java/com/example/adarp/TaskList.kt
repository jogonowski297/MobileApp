package com.example.adarp

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class TaskList(private val context: Activity, internal var tasks: List<Task>) : ArrayAdapter<Task>(context, R.layout.layout_list_task, tasks) {


        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

                val inflater = context.layoutInflater
                val listViewItem = inflater.inflate(R.layout.layout_list_task, null, true)
                val idtask = listViewItem.findViewById(R.id.id_task) as TextView
                val textViewWorker = listViewItem.findViewById(R.id.textViewWorker) as TextView
                val textViewCompany = listViewItem.findViewById(R.id.textViewCompany) as TextView
                val textViewSubject = listViewItem.findViewById(R.id.textViewSubject) as TextView
                val date = listViewItem.findViewById(R.id.date) as TextView

                val imageButton = listViewItem.findViewById(R.id.imageButton) as ImageButton

                imageButton.setOnClickListener {
                        ConfirmWindow(
                                idtask.text.toString(),
                                EndPoints.URL_ADD_TASKS_CLOSED,
                                textViewWorker.text.toString(),
                                textViewCompany.text.toString(),
                                textViewSubject.text.toString(),
                                date.text.toString()
                        )
                }

//                textViewSubject.invalidate()


                val artist = tasks[position]
                idtask.text = artist.id_task
                textViewWorker.text = artist.worker
                textViewCompany.text = artist.company
                textViewSubject.text = artist.subject
                date.text = artist.date.toString()


                return listViewItem
        }

        private fun getDateTime(): String {
                val sdf = SimpleDateFormat("yyyy/M/dd hh:mm:ss")
                val currentDate = sdf.format(Date())
                return currentDate
        }

        private fun ConfirmWindow(id_: String, url: String, worker: String, company: String, sub: String, date_: String){
                MaterialAlertDialogBuilder(context)
                        .setTitle("Potwierdz usunięcie")

                        .setMessage("Czy na pewno chcesz usunąć zadanie o numerze ${id_}?")

                        .setPositiveButton("Tak"){dialog, which ->
                                addArtist(id_,url,worker,company,sub,date_)
                                Toast.makeText(context, "Zadanie zostało usunięte", Toast.LENGTH_SHORT).show()

                        }

                        .setNeutralButton("Nie"){dialog, which ->

                        }
                        .show()
        }


        //adding a new record to database
        private fun addArtist(id_: String, url: String, worker: String, company: String, sub: String, date_: String) {
                //getting the record values
                val id = id_.toInt()
                val worker_id = worker
                val company_id = company
                val subject = sub
                val date_add = date_
                val date_close = getDateTime()

                //creating volley string request
                val stringRequest = object : StringRequest(
                        Method.POST, url,
                        Response.Listener<String> { response ->
                                try {
                                        val obj = JSONObject(response)
                                        Toast.makeText(context, obj.getString("message"), Toast.LENGTH_LONG)
                                                .show()
                                } catch (e: JSONException) {
                                        e.printStackTrace()
                                }
                        },
                        object : Response.ErrorListener {
                                override fun onErrorResponse(volleyError: VolleyError) {
                                        Toast.makeText(context, volleyError.message, Toast.LENGTH_LONG)
                                                .show()
                                }
                        }) {
                        @Throws(AuthFailureError::class)
                        override fun getParams(): Map<String, String> {
                                val params = HashMap<String, String>()
                                params.put("id", id.toString())
                                params.put("worker_id", worker_id)
                                params.put("company_id", company_id)
                                params.put("subject", subject)
                                params.put("date_add", date_add)
                                params.put("date_close", date_close)
                                println("params: $params")
                                return params
                        }
                }

                //adding request to queue
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
                ViewTasksActivity().refresh()
        }

}
