package com.example.terminalweb.util

import org.springframework.security.crypto.password.PasswordEncoder
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class Encryption: PasswordEncoder {

    companion object{
        private lateinit var secretKey: SecretKeySpec
        private lateinit var key: ByteArray
        public val KEY = "softmax@2019"

        fun setKey(myKey: String){
            var sha: MessageDigest? = null
            try {
                key = myKey.toByteArray(Charsets.UTF_8)
                sha = MessageDigest.getInstance("SHA-1")
                key = sha.digest(key)
                key = Arrays.copyOf(key, 16)
                secretKey = SecretKeySpec(key, "AES")
            } catch(ex: NoSuchAlgorithmException){
                ex.printStackTrace()
            } catch (ex: UnsupportedEncodingException){
                ex.printStackTrace()
            }
        }

//        fun decryptAES(strToDecrypt: String, secret: String): String? {
//            try {
//                setKey(secret)
//                val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
//                cipher.init(Cipher.DECRYPT_MODE, secretKey)
//                return String(cipher.doFinal(Base64.decode(strToDecrypt)))
//            } catch (e: Exception) {
//                println("Error while decrypting: $e")
//            }
//            return null
//        }
    }

    override fun encode(rawPassword: CharSequence): String {
        val original = charSeqToString(rawPassword)
        try {
            val hexString = StringBuilder()
            val algorithm = MessageDigest.getInstance("MD5")
            val messageDigest = algorithm.digest(original.toByteArray(charset("UTF-8")))
            for (b in messageDigest) {
                hexString.append(String.format("%02X", 0xFF and b.toInt()))
            }
            return hexString.toString()
        } catch (ex: NoSuchAlgorithmException) {
            ex.printStackTrace()
        } catch (ex: UnsupportedEncodingException) {
            ex.printStackTrace()
        }

        return ""
    }

    override fun matches(rawPassword: CharSequence?, encodedPassword: String?): Boolean {
        return encode(rawPassword!!) == encodedPassword
    }

    private fun charSeqToString(raw: CharSequence): String {
        val sb = StringBuilder(raw.length)
        sb.append(raw)
        return sb.toString()
    }
}