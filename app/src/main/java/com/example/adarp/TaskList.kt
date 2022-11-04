package com.example.adarp

import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.content.SharedPreferences
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class TaskList(private val context: Activity, internal var tasks: List<Task>, var sharedPreference: SharedPreferences) : ArrayAdapter<Task>(context, R.layout.layout_list_task, tasks) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val inflater = context.layoutInflater
                val listViewItem = inflater.inflate(R.layout.layout_list_task, null, true)

                val cardView = listViewItem.findViewById(R.id.cardView) as CardView
                val result = sharedPreference.getString("workers", "XXX")
                println("result: $result")
                val list = Regex("\\w+")
                        .findAll(result.toString())
                        .toList()
                        .map { it.value }
                val workerName = tasks[position].getWorkerTask()

                for (i in list){
                        if(workerName == i) {
                                val color = sharedPreference.getString("${i}_color", "#FF00C9")
                                println("color: $color")
                                cardView.setCardBackgroundColor(Color.parseColor(color))
                        }
                }

                val idtask = listViewItem.findViewById(R.id.id_task) as TextView
                val textViewWorker = listViewItem.findViewById(R.id.textViewWorker) as TextView
                val textViewCompany = listViewItem.findViewById(R.id.textViewCompany) as TextView
                val textViewSubject = listViewItem.findViewById(R.id.textViewSubject) as TextView
                val date = listViewItem.findViewById(R.id.date) as TextView
                val btn_end = listViewItem.findViewById(R.id.btn_end) as Button

                btn_end.setOnClickListener {
                        showTaskInBiggerWindow(
                                idtask.text.toString(),
                                EndPoints.URL_ADD_TASKS_CLOSED,
                                textViewWorker.text.toString(),
                                textViewCompany.text.toString(),
                                textViewSubject.text.toString(),
                                date.text.toString()
                        )
                }

                val artist = tasks[position]
                idtask.text = artist.id_task
                textViewWorker.text = artist.worker
                textViewCompany.text = artist.company
                textViewSubject.text = artist.subject
                date.text = artist.date


                return listViewItem
        }

        private fun getDateTime(): String {
                val sdf = SimpleDateFormat("yyyy/M/dd hh:mm:ss")
                val currentDate = sdf.format(Date())
                return currentDate
        }


        private fun showTaskInBiggerWindow(id_task: String, url: String, worker: String, company: String, subejct: String,date: String){
                val dialogBinding = context.layoutInflater.inflate(R.layout.activity_custom_dialog_delete, null)
                val myDialog = Dialog(context)
                myDialog.setContentView(dialogBinding)
                dialogBinding.findViewById<TextView>(R.id.idTask).text = "Zadanie numer ${id_task}"
                dialogBinding.findViewById<TextView>(R.id.dateTask).text = "${date}"
                dialogBinding.findViewById<TextView>(R.id.workerTask).text = "${worker}"
                dialogBinding.findViewById<TextView>(R.id.companyTask).text = "${company}"
                dialogBinding.findViewById<TextView>(R.id.subjectTask).text = "${subejct}"

                myDialog.setCancelable(true)
                myDialog.window?.setBackgroundDrawableResource(R.drawable.round_corner_delete)

                val width = (context.resources.displayMetrics.widthPixels * 0.90).toInt()

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
                        deleteAndAddToEnded(id_task,url,worker,company,subejct,date)
                        myDialog.dismiss()

                }

                myDialog.show()
        }

        
        private fun deleteAndAddToEnded(task_id_: String, url: String, worker: String, company: String, sub: String, date_: String) {
                val task_id = task_id_
                val worker_id = sharedPreference.getString("${worker}_id", "XXX").toString()
                val company_id = company
                val subject = sub
                val date_add = date_
                val date_close = getDateTime()

                val stringRequest = object : StringRequest(
                        Method.POST, url,
                        Response.Listener { response ->
                                try {
                                        val obj = JSONObject(response)
                                        Toast.makeText(context, obj.getString("message"), Toast.LENGTH_LONG).show()

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
