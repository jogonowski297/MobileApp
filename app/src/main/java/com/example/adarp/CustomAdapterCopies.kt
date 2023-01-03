package com.example.adarp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class CustomAdapterCopies(private val mList: List<Copies>) : RecyclerView.Adapter<CustomAdapterCopies.ViewHolder>() {

    var onItemClick: ((Copies) -> Unit)? = null
    var onBtnClick: ((Copies) -> Unit)? = null
    var onBtnMenuClick: ((ImageButton) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_copies, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]

        holder.id_copy.text = ItemsViewModel.getIdCopies().toString()
        holder.textViewDate.text = ItemsViewModel.getDateCopies()
        holder.textViewRozmiar.text = ItemsViewModel.getDateCopies()
        holder.textViewKopia.text = ItemsViewModel.getCopyCopies()
        holder.textViewNazwa.text = ItemsViewModel.getNameCopies()
        holder.oneCopy.setBackgroundColor(Color.parseColor(ItemsViewModel.getColorCopies()))
        holder.oneCopyBackground.setBackgroundColor(Color.parseColor(ItemsViewModel.getColorCopies()))
    }


    override fun getItemCount(): Int {
        return mList.size
    }


    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView){
        var id_copy: TextView = itemView.findViewById(R.id.id_copy)
        var textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
        var textViewRozmiar: TextView = itemView.findViewById(R.id.textViewRozmiar)
        var textViewKopia: TextView = itemView.findViewById(R.id.textViewKopia)
        var textViewNazwa: TextView = itemView.findViewById(R.id.textViewNazwa)
        var oneCopy: LinearLayout = itemView.findViewById(R.id.one_copy)
        var oneCopyBackground: LinearLayout = itemView.findViewById(R.id.one_copy_background)

        init{
            ItemView.setOnClickListener {
                onItemClick?.invoke(mList[adapterPosition])
            }
        }


    }


}
