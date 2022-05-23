package com.drakjoakas.myqrscanner

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.drakjoakas.myqrscanner.helper.MailHelper
import com.drakjoakas.myqrscanner.helper.PatternHelper
import me.dm7.barcodescanner.zxing.ZXingScannerView
import com.google.zxing.Result
import ezvcard.Ezvcard
import ezvcard.VCard
import ezvcard.io.text.VCardReader
import ezvcard.property.Telephone


class QR : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private var scannerView: ZXingScannerView? = null
    private val PERMISO_CAMARA = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        comenzarCamara()
    }

    private fun tienePermiso(): Boolean {
        val permiso = android.Manifest.permission.CAMERA
        return (ContextCompat.checkSelfPermission(this@QR, permiso) == PackageManager.PERMISSION_GRANTED)
    }

    private fun solicitarPermiso() {
        val permiso = android.Manifest.permission.CAMERA
        ActivityCompat.requestPermissions(this@QR, arrayOf(permiso),PERMISO_CAMARA)
    }

    private fun comenzarCamara() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !tienePermiso()) {
            solicitarPermiso()
        }

        if(scannerView == null) {
            scannerView = ZXingScannerView(this)
            setContentView(scannerView)
        }

        scannerView?.setResultHandler(this)
        scannerView?.startCamera()
    }

    override fun handleResult(p0: Result?) {

        val patternHelper = PatternHelper()

        if (p0 != null) {
            val entrada = p0.text
            if (patternHelper.esPatronValido(entrada)){
                when (patternHelper.obtenerTipoPatron(entrada)) {
                    0 -> { //SMS
                        mandarSMS(entrada)
                    }
                    1 -> { //VCARD
                        guardarContacto(entrada)
                    }
                    2 -> { //MAIL
                        enviarEmail(entrada)
                    }
                    3 -> { //WEB
                        lanzarWeb(entrada)
                    }
                    else -> {
                        mensajeError()
                    }
                }
                scannerView?.stopCamera()
                finish()
            } else {
                mensajeError()
            }
        }


    }

    override fun onResume() {
        super.onResume()
        comenzarCamara()
    }

    private fun lanzarWeb(url: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    private fun guardarContacto(vcard: String) {
        val reader = Ezvcard.parse(vcard).first()

        val telefonos = reader.telephoneNumbers.filter {
            return@filter it.text.isNotEmpty()
        }

        val emails = reader.emails.filter {
            return@filter it.value.isNotEmpty()
        }


        val i = Intent(Intent.ACTION_INSERT).apply {
            type = ContactsContract.RawContacts.CONTENT_TYPE
            putExtra(ContactsContract.Intents.Insert.NAME,reader.formattedName.value)

        }

        for (tel in telefonos) {
            i.putExtra(ContactsContract.Intents.Insert.PHONE,tel.text)
        }
        for (mail in emails) {
            i.putExtra(ContactsContract.Intents.Insert.EMAIL,mail.value)
        }

        startActivity(i)

    }
    private fun mandarSMS(sms: String) {
        sms.lowercase()
        val idBody = sms.indexOf(":",sms.indexOf(":")+1)+1
        val uri2   = sms.substring(0,idBody).lowercase()
        val body   = sms.substring(idBody)
        val uri    = Uri.parse(uri2)
        val i      = Intent(Intent.ACTION_SENDTO,uri)
        i.putExtra("sms_body",body)
        startActivity(i)


    }

    private fun enviarEmail(email:String) {

        val helper = MailHelper(email)

        val mailDict = helper.getDict()
        if (mailDict != null) {
            val emails = arrayOf(mailDict["address"])

            val i = Intent(Intent.ACTION_SEND).apply {
                type = "*/*"
                putExtra(Intent.EXTRA_EMAIL,emails)
                putExtra(Intent.EXTRA_SUBJECT,mailDict["subject"])
                putExtra(Intent.EXTRA_TEXT,mailDict["body"])
            }


            lanzarIntent(i)
        } else {
            Toast.makeText(this@QR,"Diccionario vacio",Toast.LENGTH_LONG).show()
        }





    }


    private fun lanzarIntent(i: Intent) {
        if (i.resolveActivity(packageManager) != null){
            startActivity(i)
        } else {
            Toast.makeText(
                this@QR,
                "No hay aplicación para manejar el intent",
                Toast.LENGTH_LONG).show()
        }
    }

    private fun mensajeError() {
        AlertDialog.Builder(this@QR)
            .setTitle(R.string.mensaje_error_titulo)
            .setMessage(R.string.mensaje_de_error)
            .setPositiveButton(
                R.string.cerrar_dialog
            ) { dialogInterface, i ->
                dialogInterface.dismiss()
                finish()
            }
            .create()
            .show()
    }

}