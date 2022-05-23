package com.drakjoakas.myqrscanner.helper

import android.net.MailTo
import android.util.Log


class MailHelper(_mail: String) {

    private val mail = _mail

    private fun matmsgToDict(): Map<String,String> {

        val email = mail.substring(
            mail.indexOf("TO:")+3,
            mail.indexOf(";SUB:")
        )
        val subj = mail.substring(
            mail.indexOf("SUB:")+4,
            mail.indexOf(";BODY:")
        )
        val body = mail.substring(
            mail.indexOf("BODY:")+5,
            mail.indexOf(";;")
        )
        return mapOf<String,String>(
            "address" to email,
            "subject" to subj,
            "body"    to body
        )

    }

    private fun mailtoToDict(): Map<String,String> {

        val mailTO = MailTo.parse(mail)

        val email = mailTO.to
        val subj = mailTO.subject
        val body = mailTO.body
        return mapOf<String,String>(
            "address" to email,
            "subject" to subj,
            "body"    to body
        )
    }

    fun getDict(): Map<String,String>? {
        if (mail.indexOf("mailto:") > -1) return mailtoToDict()

        if (mail.indexOf("MATMSG:") > -1) return matmsgToDict()

        return null

    }
}