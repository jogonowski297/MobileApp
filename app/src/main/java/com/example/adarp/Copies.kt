package com.example.adarp

class Copies(val id_copies: Int, val nazwa: String, val rozmiar: String, val data_: String, val kopia: String, val color: String) {

    fun getIdCopies(): Int {
        return id_copies
    }

    fun getNameCopies(): String {
        return nazwa
    }

    fun getSizeCopies(): String {
        return rozmiar
    }

    fun getDateCopies(): String {
        return data_
    }

    fun getCopyCopies(): String {
        return kopia
    }

    fun getColorCopies(): String {
        return color
    }
}
