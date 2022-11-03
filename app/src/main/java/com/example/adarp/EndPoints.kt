package com.example.adarp

object EndPoints {
    private val URL_ROOT = "https://adarp.pl/app/v1/?op="
    val URL_ADD_ARTIST = URL_ROOT + "addartist"
    val URL_GET_ARTIST = URL_ROOT + "getartists"
    val URL_ADD_TASKS_CLOSED = URL_ROOT + "addclosedtask"
    val URL_GET_TASKS_CLOSED = URL_ROOT + "gettasksclosed"


    val URL_ADD_ARTIST_1 = "https://adarp.pl/app/postSomething/addTask.php"
    val URL_ADD_TASKS_CLOSED_1 = "https://adarp.pl/app/postSomething/addClosedTask.php"


    val URL_GET_COMPANY = "https://adarp.pl/app/spinner/companySpinner.php"
    val URL_GET_COLOR_USERS = "https://adarp.pl/app/getSomething/getColor.php"
    val URL_GET_WORKERS = "https://adarp.pl/app/spinner/workersSpinner.php"
    val URL_GET_TASKS = "https://adarp.pl/app/listView/tasksView.php"
//    val URL_GET_TASKS = "https://crm.adarp.eu/crm_mobile/app/listView/tasksView.php"
    val URL_GET_COPIES = "https://adarp.pl/app/listView/copiesView.php"


}