package com.cnst.smartmemogame

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

    fun isPermited(   context: Context,  permisson:String):Boolean{
        return      ContextCompat.checkSelfPermission(context,permisson)==PackageManager.PERMISSION_GRANTED
    }
    fun askpermission(activity: Activity?,permisson: String,rcode:Int){
        if (activity != null) {
            ActivityCompat.requestPermissions(activity, arrayOf(permisson),rcode)
        }
    }

