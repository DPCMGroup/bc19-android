package com.example.tagnfckotlin

class WorkstationModelClass {
    var id: String? = null
    var workstationname: String? = null
    var tag: String? = null
    var xworkstation: Int? = null
    var yworkstation: Int? = null
    var idroom: Int? = null
    var state: Int?=  null
    var archived: Int?=  null

    constructor(id: String?, workstationname: String?, tag: String?,  xworkstation: Int?, yworkstation: Int?, state: Int?, idroom: Int?, archived: Int?) {
        this.id = id
        this.workstationname = workstationname
        this.tag = tag
        this.xworkstation = xworkstation
        this.yworkstation = yworkstation
        this.idroom = idroom
        this.state = state
        this.archived = archived
    }

    constructor() {}
}