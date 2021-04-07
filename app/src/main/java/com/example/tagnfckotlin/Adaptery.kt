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
        holder.WorkstationId.text = mdata[position].WorkstationId
        holder.Status.text = mdata[position].Status
        holder.BookedBy.text = mdata[position].BookedBy


    }

    override fun getItemCount(): Int {
        return mdata.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var WorkstationId: TextView
        var Status: TextView
        var BookedBy: TextView


        init {
            WorkstationId = itemView.findViewById(R.id.workstationid_txt)
            Status = itemView.findViewById(R.id.status_txt)
            BookedBy = itemView.findViewById(R.id.bookedby_txt)

        }
    }
}