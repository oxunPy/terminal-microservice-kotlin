package com.example.common.util

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class Encoder {
    companion object{
        fun convert(original: String): String{
            try {
                val hexString: StringBuilder = StringBuilder()
                val algorithm = MessageDigest.getInstance("MD5")
                val messageDigest = algorithm.digest(original.toByteArray(charset("UTF-8")))

                for(b: Byte in messageDigest){
                    hexString.append("${0xFF and b.toInt()}02X")
                }
                return hexString.toString()
            } catch(ex: Exception){
                println("Error converting user password \n" + ex.message)
            }
            return ""
        }

    }
}