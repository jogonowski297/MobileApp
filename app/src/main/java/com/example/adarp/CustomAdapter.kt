package com.example.adarp

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class CustomAdapter(ctx: Context, list: ArrayList<Model>): ArrayAdapter<Model>(ctx,0,list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return myView(position,convertView,parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return myView(position,convertView,parent)
    }

    private fun myView(position: Int, convertView: View?, parent: ViewGroup): View {
        val list = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.item,
            parent,
            false
        )

        list?.let{
            val txt = view.findViewById<TextView>(R.id.textView3)
            txt.text = list.name!!

            txt.setTextColor(Color.parseColor("#000000"))
            if(list.id==0)
                txt.setTextColor(Color.parseColor("#A3A3A3"))
        }

        return view
    }

}
