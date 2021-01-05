package com.cnst.smartmemogame

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cnst.smartmemogame.databinding.ActivityMainGameBinding
import com.cnst.smartmemogame.models.BoardSize
import com.cnst.smartmemogame.models.GameLogic
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainGame : AppCompatActivity() {
    private  lateinit var bind:ActivityMainGameBinding
    private lateinit var linearLayout: LinearLayout
    private  lateinit var card1:CardView
    private  lateinit var card2:CardView
    private lateinit var moves:TextView
    private  var  database= Firebase.firestore
    private  var  gamename:String?=null
    private lateinit var pairs:TextView
    var level:String?=null
    private  lateinit var gameLogic: GameLogic
    private  var CusimgList:List<String>?=null
    private lateinit var adaptor: MainGameAdaptor
    private lateinit var recycle:RecyclerView
private lateinit var size :BoardSize
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind=ActivityMainGameBinding.inflate(layoutInflater)
        setContentView(bind.root)
        linearLayout=bind.Bott
        card1=bind.card1
        card2=bind.card2
        moves=bind.moves
        pairs=bind.matchs
        recycle=bind.recycle

        level=intent.getStringExtra("level").toString()
        var gamename=intent.getStringExtra("gamename").toString()



          downloadGame(gamename)



    }

    override fun onBackPressed() {

        MaterialAlertDialogBuilder(this).setTitle("Do you really want to go back?").setPositiveButton("YES",DialogInterface.OnClickListener { dialogInterface, i ->
            dialogInterface.dismiss()

            finish()

        }).setNegativeButton("NO",DialogInterface.OnClickListener { dialogInterface, i ->
            dialogInterface.dismiss()
        }).show()
        return
    }
    private fun updateGame(position: Int) {

        if(gameLogic.haveWon()){
            Toast.makeText(this,"Already won", Toast.LENGTH_LONG).show()
return
        }
        if(gameLogic.isFadeup(position)){
            Toast.makeText(this,"Invalid move", Toast.LENGTH_LONG).show()
return
        }
gameLogic.flip(this,position)
        pairs.text="PAIRS: "+gameLogic.nopairsfound.toString()+"/"+size.getnummpair().toString()
        adaptor.notifyDataSetChanged()
moves.text="MOVES: "+gameLogic.mm().toString()
    }


    private fun downloadGame(Customname: String) {
        database.collection("games").document(Customname ).get().addOnSuccessListener {
                doc->
            val customimglisth=doc.toObject(CustomList::class.java)
            if(customimglisth?.images==null){

                when(level){
                    "easy"->size=BoardSize.EASY
                    "hard"->size=BoardSize.HARD
                    else->size=BoardSize.MEDIUM

                }
                gameLogic= GameLogic(size, CusimgList)

                adaptor=MainGameAdaptor(this,size,gameLogic.cards,object : MainGameAdaptor.cardclkListen{
                    override fun OnClicked(position: Int) {
                        updateGame(position)
                    }


                })
                recycle.adapter=adaptor
                recycle.layoutManager=GridLayoutManager(this,size.getWidth())
                recycle.setHasFixedSize(true)


                return@addOnSuccessListener}
            val numcards=customimglisth.images.size*2
            size= BoardSize.getBytebyval(numcards)
            CusimgList   =customimglisth.images
            gameLogic= GameLogic(size, CusimgList!!)
            gamename=Customname
            adaptor=MainGameAdaptor(this,size,gameLogic.cards,object : MainGameAdaptor.cardclkListen{
                override fun OnClicked(position: Int) {
                    updateGame(position)
                }


            })
            recycle.adapter=adaptor
            recycle.layoutManager=GridLayoutManager(this,size.getWidth())
            recycle.setHasFixedSize(true)


        }
    }
}