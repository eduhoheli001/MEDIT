package com.example.medizinische_informatik.PDF

import android.content.Context
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.StampingProperties
import com.itextpdf.signatures.*
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.File
import java.io.FileOutputStream
import java.security.PrivateKey
import java.security.Security
import java.security.cert.Certificate

fun signPdf(context: Context, inputFile: File, outputFile: File, privateKey: PrivateKey?, certificate: Certificate?) {
    if (privateKey == null || certificate == null) {
        return
    }

    try {
        Security.addProvider(BouncyCastleProvider())
        val reader = PdfReader(inputFile.absolutePath)
        val signer = PdfSigner(reader, FileOutputStream(outputFile), StampingProperties())

        val appearance = signer.signatureAppearance
        appearance.reason = "Dokumenten Signatur"
        appearance.location = "Austria"
        appearance.setReuseAppearance(false)

        val signature = PrivateKeySignature(privateKey, DigestAlgorithms.SHA256, BouncyCastleProvider.PROVIDER_NAME)
        val digest = BouncyCastleDigest()
        val chain = arrayOf(certificate)

        signer.signDetached(digest, signature, chain, null, null, null, 0, PdfSigner.CryptoStandard.CMS)

    } catch (e: Exception) {
        e.printStackTrace()
    }
}
