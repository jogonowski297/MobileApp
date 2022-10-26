package com.example.adarp

class Task(val id_task: String, val worker: String, val company: String, val subject: String, val date: String){
    fun getIdTask(): String{
        return id_task
    }

    fun getWorkerTask(): String{
        return worker
    }

    fun getCompanyTask(): String{
        return company
    }

    fun getSubjectTask(): String{
        return subject
    }

    fun getDateTask(): String{
        return date
    }

}


