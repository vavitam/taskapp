package com.example.taskapp.model

class TaskModel (
    var taskId: String? = null,
    var userId: String? = null,
    var taskTitle: String? = null,
    var taskTimeDead: String? = null,
    var taskTimeHour: String? = null,
    var taskDesc: String? = null,
    var taskImage: String? = null,
    var taskImport: Boolean? = false,
    var isChecked: Boolean? = false,
    var taskDateCom: String? = null
)