package com.example.medizinische_informatik.AESUtils
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import android.util.Base64

object AESUtils {
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"

    fun encrypt(data: String, key: SecretKey): Pair<String, String> {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val iv = ByteArray(cipher.blockSize)
        SecureRandom().nextBytes(iv)
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec)
        val encryptedData = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
        return Pair(Base64.encodeToString(encryptedData, Base64.DEFAULT), Base64.encodeToString(iv, Base64.DEFAULT))
    }

    fun decrypt(data: String, key: SecretKey, iv: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val ivSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)
        val decodedData = Base64.decode(data, Base64.DEFAULT)
        val decryptedData = cipher.doFinal(decodedData)
        return String(decryptedData, Charsets.UTF_8)
    }

    fun keyFromString(keyString: String): SecretKey {
        val decodedKey = Base64.decode(keyString, Base64.DEFAULT)
        return SecretKeySpec(decodedKey, 0, decodedKey.size, ALGORITHM)
    }
}