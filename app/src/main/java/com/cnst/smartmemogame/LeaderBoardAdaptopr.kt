package com.cnst.smartmemogame

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LeaderBoardAdaptopr(
    var context:Context,
    private var options: List<PlayerScore>?
): RecyclerView.Adapter<LeaderBoardAdaptopr.ViewHolder>() {
  inner class ViewHolder(view:View):RecyclerView.ViewHolder(view){


      var rank=view.findViewById<TextView>(R.id.rank)
      var name=view.findViewById<TextView>(R.id.name)
      var score=view.findViewById<TextView>(R.id.scoreval)
      fun bind(position: Int) {
          var r=position+1
rank.text=(r).toString()
          name.text=  options?.get(position)?.name.toString()
          score.text=  options?.get(position)?.score.toString()
      }
  }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       var v=LayoutInflater.from(context).inflate(R.layout.llb,parent,false)
        return  ViewHolder(v)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(position)
    }

    override fun getItemCount(): Int {
 return options?.size!!
    }
}