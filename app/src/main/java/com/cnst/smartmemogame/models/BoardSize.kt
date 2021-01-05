package com.cnst.smartmemogame.models

enum class BoardSize(var numcard:Int) {
    EASY(8),MEDIUM(18),HARD(24);
    fun getWidth():Int{
        return  when(this){
            EASY -> 2
            MEDIUM -> 3
            HARD -> 4
        }
    }
    companion object{
        fun  getBytebyval(value:Int) = values().first { it.numcard==value }
    }
    fun getHeight():Int{
        return  numcard/  getWidth()
    }
    fun getnummpair():Int{
        return  numcard/2
    }


}