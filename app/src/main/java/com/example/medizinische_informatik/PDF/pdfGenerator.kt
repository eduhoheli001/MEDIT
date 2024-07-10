package com.example.medizinische_informatik.PDF

import android.content.Context
import android.os.Environment
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import java.io.File


fun generatePdf(
    context: Context,
    physicianList: List<String>,
    mentalDataList: List<String>,
    nutritionDataList: List<String>,
    sportDataList: List<String>,
    medicalDataList: List<String>,
    allergiesList: List<String>
) {
    val pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
    val file = File(pdfPath, "signiertesPDF.pdf")

    val writer = PdfWriter(file)
    val pdfDoc = PdfDocument(writer)
    val document = com.itextpdf.layout.Document(pdfDoc)

    val title = com.itextpdf.layout.element.Paragraph("Benutzerdaten")
        .setFontSize(24f)
        .setBold()

    document.add(title)

    if (physicianList.isNotEmpty()) {
        val physicianTitle = com.itextpdf.layout.element.Paragraph("Physician List").setBold()
        document.add(physicianTitle)
        physicianList.forEach { physician ->
            val physicianInfo = com.itextpdf.layout.element.Paragraph(physician)
            document.add(physicianInfo)
        }
    }

    if (mentalDataList.isNotEmpty()) {
        val mentalDataTitle = com.itextpdf.layout.element.Paragraph("Mental Data").setBold()
        document.add(mentalDataTitle)
        mentalDataList.forEach { data ->
            val mentalDataInfo = com.itextpdf.layout.element.Paragraph(data)
            document.add(mentalDataInfo)
        }
    }

    if (nutritionDataList.isNotEmpty()) {
        val nutritionDataTitle = com.itextpdf.layout.element.Paragraph("Nutrition Data").setBold()
        document.add(nutritionDataTitle)
        nutritionDataList.forEach { data ->
            val nutritionDataInfo = com.itextpdf.layout.element.Paragraph(data)
            document.add(nutritionDataInfo)
        }
    }

    if (sportDataList.isNotEmpty()) {
        val sportDataTitle = com.itextpdf.layout.element.Paragraph("Sport Data").setBold()
        document.add(sportDataTitle)
        sportDataList.forEach { data ->
            val sportDataInfo = com.itextpdf.layout.element.Paragraph(data)
            document.add(sportDataInfo)
        }
    }

    if (medicalDataList.isNotEmpty() || allergiesList.isNotEmpty()) {
        val medDataTitle = com.itextpdf.layout.element.Paragraph("Medical Data").setBold()
        document.add(medDataTitle)
        medicalDataList.forEach { data ->
            val medDataInfo = com.itextpdf.layout.element.Paragraph(data)
            document.add(medDataInfo)
        }
        allergiesList.forEach { data ->
            val allergyDataInfo = com.itextpdf.layout.element.Paragraph(data)
            document.add(allergyDataInfo)
        }
    }

    document.add(com.itextpdf.layout.element.Paragraph("Signiertes PDF").setBold())
    document.add(com.itextpdf.layout.element.Paragraph("Dieses Formular wurde biometrisch unterschrieben!"))

    document.close()

    // Initialisierung von privateKey und certificate
    val privateKey = loadPrivateKeyFromKeystore(context)
    val certificate = loadCertificateFromKeystore(context)

    // Signieren des PDFs
    signPdf(context, file, File(pdfPath, "signiertesPDF_signed.pdf"), privateKey, certificate)
}



/*
fun generatePdf(context: Context) {
    val pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
    val file = File(pdfPath, "arztbrief.pdf")




    val writer = PdfWriter(file)
    val pdfDoc = PdfDocument(writer)
    val document = Document(pdfDoc)

    val title = Paragraph("Überschrift")
        .setFontSize(24f)
        .setBold()

    val content = Paragraph("Das ist ein Beispieltext für die PDF-Generierung.")

    document.add(title)
    document.add(content)

    document.close()
    Toast.makeText(context, "PDF wurde erstellt", Toast.LENGTH_SHORT).show()
}*/