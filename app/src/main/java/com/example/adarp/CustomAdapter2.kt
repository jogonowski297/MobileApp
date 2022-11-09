package com.example.adarp

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.View.OnTouchListener
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class CustomAdapter2(private val mList: List<Task>) : RecyclerView.Adapter<CustomAdapter2.ViewHolder>() {

    var onItemClick: ((Task) -> Unit)? = null
    var onBtnClick: ((Task) -> Unit)? = null
    var onBtnMenuClick: ((ImageButton) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_task_list, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]

        holder.id_task.text = ItemsViewModel.getIdTask()
        holder.textViewWorker.text = ItemsViewModel.getWorkerTask()
        holder.textViewCompany.text = ItemsViewModel.getCompanyTask()
        holder.textViewSubject.text = ItemsViewModel.getSubjectTask()
        holder.textViewDate.text = ItemsViewModel.getDateTask()
        holder.oneTask.setBackgroundColor(Color.parseColor(ItemsViewModel.getTaskColor()))

    }


    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView){
        var id_task: TextView = itemView.findViewById(R.id.id_task)
        var textViewWorker: TextView = itemView.findViewById(R.id.textViewWorker)
        var textViewCompany: TextView = itemView.findViewById(R.id.textViewCompany)
        var textViewSubject: TextView = itemView.findViewById(R.id.textViewSubject)
        var textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
        var oneTask: LinearLayout = itemView.findViewById(R.id.one_task)
//        var btn_end: Button = itemView.findViewById(R.id.btn_end)
        var btn_menu: ImageButton = itemView.findViewById(R.id.btn_menu)

        init{
            ItemView.setOnClickListener {
                onItemClick?.invoke(mList[adapterPosition])
            }

//            btn_end.setOnClickListener {
//                onBtnClick?.invoke(mList[adapterPosition])
//            }

            btn_menu.setOnClickListener {
                onBtnMenuClick?.invoke(btn_menu)
            }

        }


    }


}
