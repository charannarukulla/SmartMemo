package com.cnst.smartmemogame

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cnst.smartmemogame.databinding.ActivityGameHomeBinding
import com.cnst.smartmemogame.models.BoardSize
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class GameHome : AppCompatActivity() {
    private  lateinit var  easy:Button
    private  lateinit var medium:Button
    private  lateinit var hard:Button
    private  lateinit var  score:TextView
    private lateinit var bind:ActivityGameHomeBinding
    private  var  database=Firebase.firestore
    private  var  gamename:String?=null
    private lateinit var size :BoardSize
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        bind= ActivityGameHomeBinding.inflate(layoutInflater)
        setContentView(bind.root)
        easy=bind.easy
        medium=bind.medium
        hard=bind.hard
        score=bind.score
        size=BoardSize.EASY
        easy.setOnClickListener {
            var i=Intent(this,MainGame::class.java)
            i.putExtra("level","easy")
            startActivity(i)


        }
        medium.setOnClickListener {
            var i=Intent(this,MainGame::class.java)
            i.putExtra("level","medium")
            startActivity(i)


        }
        hard.setOnClickListener {
            var i=Intent(this,MainGame::class.java)
            i.putExtra("level","hard")
            startActivity(i)


        }
  var auth=FirebaseAuth.getInstance()
        var scoretext=0
        var scored=FirebaseFirestore.getInstance().collection("users").document(auth.currentUser?.uid.toString()).get().addOnSuccessListener {

            documentSnapshot ->


            scoretext= documentSnapshot.getLong("score")?.toInt()!!
            score.text="Your score is "+scoretext.toString()
        }
        score.text="Loading...."

    }

    fun openll(view: View) {
      var i=Intent(this,LeaderBoard::class.java)
        startActivity(i)

    }
companion object{
  private const val MY_GAME=594
}


    fun owngameac(view: View) {
val diallog= MaterialAlertDialogBuilder(this)
       var options: Array<String> = arrayOf("EASY","MEDIUM","HARD")
        var seelcted=3
        var levelselected="easy"
        diallog.setTitle("Choose Your game level").setSingleChoiceItems(options,seelcted,
            DialogInterface.OnClickListener { dialogInterface, i ->

               if (i==1)

 levelselected ="medium"
                else if(i==2)
                   levelselected="hard"

                var i=Intent(this,OwnGame::class.java)
                i.putExtra("level",levelselected)
                startActivityForResult(i,MY_GAME)
                dialogInterface.dismiss()
            }).show()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== MY_GAME &&resultCode== Activity.RESULT_OK){
            var gamename=data?.getStringExtra(EXTRA_GAME_NAM)
            if(gamename==null){
                Toast.makeText(this,"Something went wrong",Toast.LENGTH_LONG).show()
                return
            }
            var i=Intent(this,MainGame::class.java)
            i.putExtra("gamename",gamename)
            startActivity(i)
        }
    }

    fun donw(view: View) {

        var v=LayoutInflater.from(this).inflate(R.layout.gname,null)
       var d= MaterialAlertDialogBuilder(this)
        d.setTitle("Enter game name")
        d.setView(v)
            d.setPositiveButton("GO",DialogInterface.OnClickListener { dialogInterface, i ->
                var g=v.findViewById<EditText>(R.id.dname).text.toString()
                database.collection("games").document(g).get().addOnSuccessListener { document ->
                    if (document != null && document.data != null) {
                        dialogInterface.dismiss()
                        gamename=g
                        var i=Intent(this,MainGame::class.java)
                        i.putExtra("gamename",gamename)
                        startActivity(i)

                    } else {
                        dialogInterface.dismiss()
                        AlertDialog.Builder(this)
                            .setTitle("Name not found")
                            .setMessage("The name you entered does not match with our record. Please choose another")
                            .setPositiveButton("OK", null)
                            .show()
                    }
                }.addOnFailureListener {exception ->

                    Toast.makeText(this, "Encountered error while saving memory game", Toast.LENGTH_SHORT).show()
                }
            })
d.setNegativeButton("Cancel",DialogInterface.OnClickListener{
    dialogInterface, i -> dialogInterface.dismiss()
})
        d.show()
    }

}