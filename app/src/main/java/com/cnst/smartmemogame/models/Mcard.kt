package com.cnst.smartmemogame.models

data class Mcard (
    val identifier:Int,
    var imgurl:String?=null,
            var isUp:Boolean = false,
                    var isMatch: Boolean =false
)