package com.cnst.smartmemogame

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cnst.smartmemogame.databinding.ActivityOwnGameBinding
import com.cnst.smartmemogame.models.BoardSize
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream

class OwnGame : AppCompatActivity() {
    lateinit var bind:ActivityOwnGameBinding
    private lateinit var size : BoardSize
    private var imgsreuired=-1

    private  lateinit var gamename:EditText
    private lateinit var save:Button
    private  lateinit var adp:ImgAdaptor
    private  lateinit var recyclerview:RecyclerView
private    var imguris= mutableListOf<Uri>()
    private    var uploadedimguris= mutableListOf<String>()
    private  var database= Firebase.firestore
    private  var disk=Firebase.storage
    override fun onCreate(savedInstanceState: Bundle?) {
        bind= ActivityOwnGameBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(bind.root)
        gamename=bind.gamename
        save=bind.save
        recyclerview=bind.rr
        save.isEnabled=false
        stopanimate()
        var level=intent.getStringExtra("level").toString()

        when(level){
            "easy"->size=BoardSize.EASY
            "hard"->size=BoardSize.HARD
            else->size=BoardSize.MEDIUM

        }

recyclerview.layoutManager=GridLayoutManager(this,size.getWidth())
        recyclerview.setHasFixedSize(true)

        imgsreuired=size.getnummpair()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title="Choose pics (0/$imgsreuired)"

        adp=ImgAdaptor(this,imguris,size, object : ImgAdaptor.ClickInt {
            override fun ClickList() {
                 if (isPermited(this@OwnGame,PIC_PER))
                     launchIntentforpic()
                 else{
                     askpermission(this@OwnGame, PIC_PER,PIC_PER_RE)
                 }
            }



        } )
        recyclerview.adapter=adp
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== PIC_PER_RE&&grantResults.isNotEmpty()&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
            launchIntentforpic()

        }
        else{
            Toast.makeText(this,"OOPS! Need this permission to make your game",Toast.LENGTH_LONG).show()
        }
    }
companion object{
    private var GET_PICS=954
    private   var PIC_PER=android.Manifest.permission.READ_EXTERNAL_STORAGE
    private   var PIC_PER_RE=100
}

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId==android.R.id.home)
        {

            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    private fun launchIntentforpic() {
var i=Intent(Intent.ACTION_PICK)
        i.type="image/*"
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
        startActivityForResult(Intent.createChooser(i,"Choose an app  to select pics"),GET_PICS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode!= GET_PICS||resultCode!= Activity.RESULT_OK||data==null)
            Toast.makeText(this,"OPPS!please try again",Toast.LENGTH_LONG).show()
    else{
            var clipdata=data.clipData
            var uri=data.data
            if(clipdata!=null){
                for(i in 0 until clipdata.itemCount){
                   if(imguris.size<imgsreuired){
                       var d=clipdata.getItemAt(i)
                       imguris.add(d.uri)
                   }
                }
            }
            else if(uri!=null){
                imguris.add(uri)
            }
        }
        gamename.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                 save.isEnabled=shouldEnable()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }


        })
save.setOnClickListener {
    uploadtoCloud()


}
adp.notifyDataSetChanged()
        supportActionBar?.title="Choose pics (${imguris.size}/$imgsreuired)"
        save.isEnabled=shouldEnable()
    }
    private fun animate(){
        av_from_code.visibility= View.VISIBLE
        supportActionBar?.title="UPLOADING please wait...."
        av_from_code.playAnimation()
        av_from_code.loop(true)
        save.visibility=View.GONE
        gamename.visibility=View.GONE
        recyclerview.visibility=View.GONE
bind.textInputLayout.visibility=View.GONE
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(gamename.windowToken,0)

    }
    private fun stopanimate(){
        av_from_code.visibility= View.GONE
        supportActionBar?.title="Choose pics (${imguris.size}/$imgsreuired)"
        av_from_code.pauseAnimation()
        bind.textInputLayout.visibility=View.VISIBLE
        save.visibility=View.VISIBLE
        gamename.visibility=View.VISIBLE
        recyclerview.visibility=View.VISIBLE
    }
    private fun uploadtoCloud() {
animate()
        database.collection("games").document(gamename.text.toString()).get().addOnSuccessListener { document ->
            if (document != null && document.data != null) {
                AlertDialog.Builder(this)
                    .setTitle("Name taken")
                    .setMessage("A game already exists with the name '${gamename.text}'. Please choose another")
                    .setPositiveButton("OK", null)
                    .show()
               stopanimate()
            } else {
                handleImageUploading(gamename.text.toString())
            }
        }.addOnFailureListener {exception ->
         stopanimate()
            Toast.makeText(this, "Encountered error while saving memory game", Toast.LENGTH_SHORT).show()
        }

    }

    private fun handleImageUploading(toString: String) {
        for ((index,photoUri) in imguris.withIndex()){
            var imgbytearray=getimgbyteArray(photoUri)
            var err=false
            var filepath="images/${gamename.text.toString()}/${System.currentTimeMillis()}--${index}.jpg"
            var sref=disk.reference.child(filepath)
            sref.putBytes(imgbytearray).continueWithTask {
                    photouploadtask->
                sref.downloadUrl
            }.addOnCompleteListener { downloadtask->
                if (!downloadtask.isSuccessful){
                    stopanimate()
                    Toast.makeText(this,"Something went wrong.Try agin. make sure you are connected to internet ",Toast.LENGTH_LONG)
                        .show()
                    err=true
                    return@addOnCompleteListener
                }
                if(err){
                    return@addOnCompleteListener
                }
                var durl=downloadtask.result.toString()
                uploadedimguris.add(durl)
                if(uploadedimguris.size==imgsreuired)
                    uploadSucess(gamename,uploadedimguris)

            }
        }
    }

    private fun uploadSucess(
        gamename: EditText,
        uploadedimguris: MutableList<String>
    ) {
database.collection("games").document(gamename.text.toString()).set(mapOf("images" to uploadedimguris)).addOnCompleteListener {
    done->
    if(!done.isSuccessful){
stopanimate()
        Toast.makeText(this,"Something went wrong.Try agin. make sure you are connected to internet ",Toast.LENGTH_LONG)
            .show()

        return@addOnCompleteListener
    }
var alert=MaterialAlertDialogBuilder(this).setTitle("Game Uploaded").setMessage("Tap ok to play your game").setPositiveButton("OK:)",DialogInterface.OnClickListener { dialogInterface, i ->

    var i=Intent( )
    i.putExtra(EXTRA_GAME_NAM,gamename.text.toString())
    setResult(Activity.RESULT_OK,i)
    finish()
    dialogInterface.dismiss()
}).show()
    stopanimate()



}
    }

    private fun getimgbyteArray(photoUri: Uri): ByteArray {
        var orgbitmap=if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.P){
            val s=ImageDecoder.createSource(contentResolver,photoUri)
            ImageDecoder.decodeBitmap(s)
        }else{
            MediaStore.Images.Media.getBitmap(contentResolver,photoUri)

        }
        var newbitmap=BitmapScaller.scaleToFitHeight(orgbitmap,250)
        var outputStream=ByteArrayOutputStream()
newbitmap.compress(Bitmap.CompressFormat.JPEG,50,outputStream)
        return  outputStream.toByteArray()
    }

    private fun shouldEnable(): Boolean {

if(imguris.size==imgsreuired&&gamename.text.toString()!=""){
    return true}

        return false
    }


}