package com.example.adarp

import android.content.SharedPreferences

class MySharedPreference {
    lateinit var tasksInMemory: SharedPreferences
    lateinit var closedTasksInMemory: SharedPreferences
    lateinit var workersInMemory: SharedPreferences
    lateinit var companyInMemory: SharedPreferences
    lateinit var copiesInMemory: SharedPreferences
}