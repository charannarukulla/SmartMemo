package com.cnst.smartmemogame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class LeaderBoard : AppCompatActivity() {
lateinit var adaptopr: LeaderBoardAdaptopr

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leader_board)

        var rr=findViewById<RecyclerView>(R.id.leaderrr)
        var uid=FirebaseAuth.getInstance().currentUser?.uid
 FirebaseFirestore.getInstance().collection("users").orderBy("score",Query.Direction.DESCENDING).addSnapshotListener { value, error ->


        var playerModel=value?.map {
             it.toObject(PlayerScore::class.java)
         }

      adaptopr= LeaderBoardAdaptopr(this,playerModel)
   rr.adapter=adaptopr
   rr.setHasFixedSize(true)
           rr.layoutManager=LinearLayoutManager(this)


 }



    }

    override fun onStart() {
        super.onStart()

    }
}