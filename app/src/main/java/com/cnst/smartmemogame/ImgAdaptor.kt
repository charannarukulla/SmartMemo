package com.cnst.smartmemogame

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.cnst.smartmemogame.models.BoardSize
import kotlin.math.min

class ImgAdaptor(private val conntext: Context,private val imguris: MutableList<Uri>,private val size: BoardSize,private val listener:ClickInt) : RecyclerView.Adapter<ImgAdaptor.ViewHolder>() {


    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        var img=view.findViewById<ImageView>(R.id.imageView2)
         fun bind(imguris: Uri){
img.setImageURI(imguris)
             img.setOnClickListener { null }
        }
         fun bind(){
img.setOnClickListener {
listener.ClickList()

}
        }

    }
    interface ClickInt{
        fun ClickList()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImgAdaptor.ViewHolder {
      var view:View=LayoutInflater.from(conntext).inflate(R.layout.img_card,parent,false)
        var height=parent.height/size.getHeight()-(2* MainGameAdaptor.MARGIN_SIZE)
        var width=parent.width/size.getWidth()-(2* MainGameAdaptor.MARGIN_SIZE)
        var len= min(height,width)
        var layparams=view.findViewById<ImageView>(R.id.imageView2).layoutParams as ViewGroup.MarginLayoutParams
        layparams.setMargins(
            MainGameAdaptor.MARGIN_SIZE,
            MainGameAdaptor.MARGIN_SIZE,
            MainGameAdaptor.MARGIN_SIZE,
            MainGameAdaptor.MARGIN_SIZE
        )
        layparams.height=len
        layparams.width=len
        return  ViewHolder(view)
    }

    override fun getItemCount(): Int =size.getnummpair()

    override fun onBindViewHolder(holder: ImgAdaptor.ViewHolder, position: Int) {
if(imguris.size>position){

    holder.bind(imguris[position])
}
        else{
    holder.bind()
        }
    }
}


