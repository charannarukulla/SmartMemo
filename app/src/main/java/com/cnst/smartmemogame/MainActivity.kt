package com.cnst.smartmemogame

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.cnst.smartmemogame.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.remote.Datastore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.lang.reflect.Array.get
import java.sql.Types.NULL


class MainActivity : AppCompatActivity() {
    private val RC_SIGN_IN: Int=100
    lateinit var  auth:FirebaseAuth
private lateinit var       signInButton: SignInButton
    private lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {

       lateinit var bind:ActivityMainBinding
        super.onCreate(savedInstanceState)
        bind= ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)



        signInButton = bind.signInButton
        stopanimate()
            signInButton.setSize(SignInButton.SIZE_WIDE)
            signInButton.setOnClickListener { Login()
            animate()
            }




    }

    override fun onBackPressed() {

        System.exit(0)
    }
    private fun login() {
        var toast:Toast= Toast.makeText(this,"Logging In...",Toast.LENGTH_LONG)
        toast.show()
        var i=Intent(this,GameHome::class.java)
        startActivity(i)
        finish()

    }

    override fun onStart() {
        super.onStart()
        var app= FirebaseApp.initializeApp(this)
        auth= FirebaseAuth.getInstance()
        var  sharedpreferences = getSharedPreferences("logstat", Context.MODE_PRIVATE);

        if(!sharedpreferences.getString("status","no").equals("no")) {

            login()

        }

    }
private fun animate(){
    av_from_code.visibility= View.VISIBLE
signInButton.visibility=View.GONE
    av_from_code.playAnimation()
    av_from_code.loop(true)

}
    private fun stopanimate(){
        signInButton.visibility=View.VISIBLE
        av_from_code.pauseAnimation()
       av_from_code.visibility= View.GONE
    }
    private fun Login() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("490185531842-g4s85cfbpd0qn8g8b42foclqafc9lfqs.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)


            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
               var toast:Toast= Toast.makeText(this,"OOPS,SIGNIN FAILED :(",Toast.LENGTH_LONG)
                toast.show()
                stopanimate()
                // [END_EXCLUDE]
            }
        }

    }


    private fun firebaseAuthWithGoogle(idToken: String) {
        // [START_EXCLUDE silent]

        // [END_EXCLUDE]
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information

                    val user = auth.currentUser
                    addUserToFirestore(auth.uid)

                } else {
                    // If sign in fails, display a message to the user.

                    // [START_EXCLUDE]

                    // [END_EXCLUDE]
                    var toast:Toast= Toast.makeText(this,"OOPS,SIGNIN FAILED with firebase :(",Toast.LENGTH_LONG)
                    toast.show()
                    stopanimate()
                }

                // [START_EXCLUDE]

                // [END_EXCLUDE]
            }
    }


         @SuppressLint("CommitPrefEdits")
         fun addUserToFirestore(uid:String?){

        var st=FirebaseFirestore.getInstance().collection("users")
        var info=da()

         var id= uid.toString()
        info.name= auth.currentUser?.displayName.toString()
        info.score=0


      st.document(id).get().addOnSuccessListener{documentSnapshot: DocumentSnapshot? ->
          try {
if(!documentSnapshot?.getString("name")?.equals(NULL)!!){
    var toast:Toast= Toast.makeText(this,"Welcome back :)",Toast.LENGTH_LONG)
    toast.show()
    stopanimate()
    login()
    var  sharedpreferences = getSharedPreferences("logstat", Context.MODE_PRIVATE);

  var editor=  sharedpreferences.edit()
    editor.putString("status", "logged");

    editor.apply();
    }}catch (ee:java.lang.Exception) {


              st.document(id).set(info).addOnSuccessListener {

                  var toast:Toast= Toast.makeText(this,"Welcome :)",Toast.LENGTH_LONG)
                  toast.show()
                  stopanimate()
                  login()
                  var  sharedpreferences = getSharedPreferences("logstat", Context.MODE_PRIVATE);

                  var editor=  sharedpreferences.edit()
                  editor.putString("status", "logged");

                  editor.apply();
              }

      } }


    }


}