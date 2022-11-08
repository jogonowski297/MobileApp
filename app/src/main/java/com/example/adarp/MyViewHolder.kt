package com.example.adarp

import android.view.View
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var textViewWorker: TextView
        get() {
            return textViewWorker
        }
        set(value) {}

    fun MyViewHolder(@NonNull @org.jetbrains.annotations.NotNull itemView: View) {
        super.itemView
        textViewWorker.setText(itemView.findViewById(R.id.textViewWorker))
    }

}