package com.example.medizinische_informatik.PDF

import android.content.Context
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.security.KeyStore
import java.security.PrivateKey
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

fun loadPrivateKeyFromKeystore(context: Context): PrivateKey? {
    copyKeystoreFile(context)

    return try {
        val keystore = KeyStore.getInstance("PKCS12")
        val password = "keystore_password".toCharArray() // Custom Keystore Passwort
        val keystoreFile = File(context.filesDir, "keystore.p12") //Custom key
        keystore.load(FileInputStream(keystoreFile), password)
        keystore.getKey("alias_of_private_key", password) as PrivateKey
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun loadCertificateFromKeystore(context: Context): X509Certificate? {
    copyKeystoreFile(context)

    return try {
        val certificateFactory = CertificateFactory.getInstance("X.509")
        val keystoreFile = File(context.filesDir, "keystore.p12")
        FileInputStream(keystoreFile).use { certInput ->
            certificateFactory.generateCertificate(certInput) as X509Certificate
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun copyKeystoreFile(context: Context) {
    var inputStream: InputStream? = null
    var outputStream: FileOutputStream? = null
    try {
        inputStream = context.assets.open("keystore.p12")
        val outFile = File(context.filesDir, "keystore.p12")
        outputStream = FileOutputStream(outFile)

        val buffer = ByteArray(1024)
        var read: Int
        while (inputStream.read(buffer).also { read = it } != -1) {
            outputStream.write(buffer, 0, read)
        }
        inputStream.close()
        outputStream.flush()
        outputStream.close()
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        inputStream?.close()
        outputStream?.close()
    }
}
