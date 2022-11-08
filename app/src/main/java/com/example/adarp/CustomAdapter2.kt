package com.example.adarp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter2(private val mList: List<Task>) : RecyclerView.Adapter<CustomAdapter2.ViewHolder>() {

    var onItemClick: ((Task) -> Unit)? = null
    var onBtnClick: ((Task) -> Unit)? = null


    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_task_list, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]

        // sets the text to the textview from our itemHolder class
        holder.id_task.text = ItemsViewModel.getIdTask()
        holder.textViewWorker.text = ItemsViewModel.getWorkerTask()
        holder.textViewCompany.text = ItemsViewModel.getCompanyTask()
        holder.textViewSubject.text = ItemsViewModel.getSubjectTask()
        holder.textViewDate.text = ItemsViewModel.getDateTask()
        holder.oneTask.setBackgroundColor(Color.parseColor(ItemsViewModel.getTaskColor()))

    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }


    // Holds the views for adding it to image and text
    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView), View.OnClickListener, View.OnLongClickListener{
        var id_task: TextView = itemView.findViewById(R.id.id_task)
        var textViewWorker: TextView = itemView.findViewById(R.id.textViewWorker)
        var textViewCompany: TextView = itemView.findViewById(R.id.textViewCompany)
        var textViewSubject: TextView = itemView.findViewById(R.id.textViewSubject)
        var textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
        var oneTask: LinearLayout = itemView.findViewById(R.id.one_task)
        var btn_end: Button = itemView.findViewById(R.id.btn_end)

        init{
            ItemView.setOnClickListener {
                onItemClick?.invoke(mList[adapterPosition])
            }

            btn_end.setOnClickListener {
                onBtnClick?.invoke(mList[adapterPosition])
            }

        }



        override fun onClick(v: View?) {
            println("TU TUTUT TUTU: $btn_end")

        }

        override fun onLongClick(v: View?): Boolean {
            TODO("Not yet implemented")
        }


    }


}
