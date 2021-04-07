package com.example.tagnfckotlin

class WorkstationModelClass {
    var WorkstationId: String? = null
    var Xposition: Int? = null
    var Yposition: Int? = null
    var Status: String?=  null

    constructor(WorkstationId: String?, Xposition: Int?, Yposition: Int?, Status: String?) {
        this.WorkstationId = WorkstationId
        this.Xposition = Xposition
        this.Yposition = Yposition
        this.Status = Status
    }

    constructor() {}
}