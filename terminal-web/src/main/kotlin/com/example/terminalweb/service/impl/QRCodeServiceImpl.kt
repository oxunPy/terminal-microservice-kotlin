package com.example.terminalweb.service.impl

import com.example.terminalweb.service.QRCodeService
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.io.IOException

@Service("QRCodeService")
class QRCodeServiceImpl(private val logger: Logger = LoggerFactory.getLogger(QRCodeServiceImpl::class.java.name)) : QRCodeService {

    override fun generateQRCode(qrContent: String?, width: Int, height: Int): ByteArray? {
        try {
            val qrCodeWriter = QRCodeWriter()
            val bitMatrix: BitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, width, height)
            val byteArrayOutputStream = ByteArrayOutputStream()
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", byteArrayOutputStream)
            return byteArrayOutputStream.toByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return null
    }
}