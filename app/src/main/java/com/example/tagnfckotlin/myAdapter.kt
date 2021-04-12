package com.example.tagnfckotlin

import android.content.Context
import android.graphics.ColorSpace
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class myAdapter (var mCtx:Context, var resources: Int, var items:List<Model>):ArrayAdapter<Model>(mCtx, resources,items){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater:LayoutInflater = LayoutInflater.from(mCtx)
        val view:View= layoutInflater.inflate(resources,null)
        val testolista:TextView=view.findViewById(R.id.testolista)


        var mItem:Model = items[position]
        testolista.text=mItem.description
        return view
    }
}

