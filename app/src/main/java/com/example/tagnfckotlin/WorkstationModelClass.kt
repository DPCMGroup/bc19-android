package com.example.tagnfckotlin

class WorkstationModelClass {
    var WorkstationId: String? = null
    var Xposition: Int? = null
    var Yposition: Int? = null
    var Status: String?=  null
    var BookedBy: String?=  null

    constructor(WorkstationId: String?, Xposition: Int?, Yposition: Int?, Status: String?, BookedBy: String?) {
        this.WorkstationId = WorkstationId
        this.Xposition = Xposition
        this.Yposition = Yposition
        this.Status = Status
        this.BookedBy = BookedBy
    }

    constructor() {}
}