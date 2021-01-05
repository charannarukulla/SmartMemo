package com.cnst.smartmemogame

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.cnst.smartmemogame.models.BoardSize
import com.cnst.smartmemogame.models.Mcard
import com.squareup.picasso.Picasso
import kotlin.math.min

class MainGameAdaptor(
    var context: Context,
    private var noofcards: BoardSize,
    private val   randimgs: List<Mcard>,
    private val clicklis:cardclkListen
) : RecyclerView.Adapter<MainGameAdaptor.ViewHolder>() {
  inner  class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        private var  imgbtn=view.findViewById<ImageView >(R.id.cardbtn)
        fun bindview(position: Int) {
            var p=randimgs[position]
            if(p.isUp){
                if(p.imgurl!=null){

                    Picasso.get().load(p.imgurl).into(imgbtn)
                }
                else{
                    imgbtn.setImageResource(p.identifier)
                }
            }
            else{
                imgbtn.setImageResource(  R.color.black)
            }

            imgbtn.alpha=if(p.isMatch) .4f else 1.0f
        val c=     if(p.isMatch) ContextCompat.getColorStateList(context,R.color.gray) else null
            ViewCompat.setBackgroundTintList(imgbtn,c)
            imgbtn.setOnClickListener {

                clicklis.OnClicked(position)
            }
        }

    }
companion object{
    const val MARGIN_SIZE=10
}
    interface  cardclkListen{

        fun OnClicked(position: Int)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        var height=parent.height/noofcards.getHeight()-(2* MARGIN_SIZE)
        var width=parent.width/noofcards.getWidth()-(2* MARGIN_SIZE)
        var len=min(height,width)
      var view=   LayoutInflater.from(context).inflate(R.layout.cards_item,parent,false)
var layparams=view.findViewById<CardView>(R.id.card).layoutParams as ViewGroup.MarginLayoutParams
        layparams.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE)
        layparams.height=len
        layparams.width=len

       return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return noofcards.numcard
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindview(position)
    }

}
