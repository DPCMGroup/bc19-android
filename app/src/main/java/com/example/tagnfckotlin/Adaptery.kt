package com.example.tagnfckotlin


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class Adaptery(private val mContext: Context, private val mdata: List<WorkstationModelClass>) :
    RecyclerView.Adapter<Adaptery.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v: View
        val inflater = LayoutInflater.from(mContext)
        v = inflater.inflate(R.layout.workstations_item, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tag.text = mdata[position].tag
        holder.state.text = mdata[position].state.toString()
      //  holder.BookedBy.text = mdata[position].BookedBy


    }

    override fun getItemCount(): Int {
        return mdata.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tag: TextView
        var state: TextView
       // var BookedBy: TextView


        init {
            tag = itemView.findViewById(R.id.workstationid_txt)
            state = itemView.findViewById(R.id.status_txt)
        //    BookedBy = itemView.findViewById(R.id.bookedby_txt)

        }
    }
}