package com.example.adarp

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


class CopiesList(private val context: Activity, internal var copies: List<Copies>) : ArrayAdapter<Copies>(context, R.layout.activity_copies, copies) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater = context.layoutInflater
        val listViewItem = inflater.inflate(R.layout.activity_copies, null, true)
        val idcopies = listViewItem.findViewById(R.id.id_copy) as TextView
        val textViewNazwa = listViewItem.findViewById(R.id.textViewNazwa) as TextView
        val textViewRozmiar = listViewItem.findViewById(R.id.textViewRozmiar) as TextView
        val textViewData = listViewItem.findViewById(R.id.textViewDate) as TextView
        val textViewKopia = listViewItem.findViewById(R.id.textViewKopia) as TextView


        val artist = copies[position]
        idcopies.text = artist.id_copies.toString()
        textViewNazwa.text = artist.nazwa
        textViewRozmiar.text = artist.rozmiar
        textViewData.text = artist.data_
        textViewKopia.text = artist.kopia


        return listViewItem
    }
}

