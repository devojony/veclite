package com.devo.veclite

class VecliteLib {
    companion object {
        // Used to load the 'veclite' library on application startup.
        init {
            System.loadLibrary("veclite")
        }
    }



}