package com.drakjoakas.myqrscanner.helper

class PatternHelper {
    private val SMS_PATTERN   = "(SMSTO:[0-9]{10}:)(.*)".toRegex()
    private val VCARD_PATTERN = "BEGIN:VCARD(\\n|.)*END:VCARD".toRegex()
    private val MAIL_PATTERN  = "MATMSG:TO:(\\n|.)*;;".toRegex()
    private val MAIL_PATTERN2 = "mailto:(.)*".toRegex()
    private val WEB_PATTERN   = "ttps?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)".toRegex()

    fun esPatronValido(entrada: String):Boolean {
        val sms  = SMS_PATTERN.containsMatchIn(entrada)
        val vcr  = VCARD_PATTERN.containsMatchIn(entrada)
        val mail = MAIL_PATTERN.containsMatchIn(entrada) || MAIL_PATTERN2.containsMatchIn(entrada)
        val web  = WEB_PATTERN.containsMatchIn(entrada)

        return sms or vcr or mail or web
    }

     fun obtenerTipoPatron(entrada: String): Int {
         return if (SMS_PATTERN.matches(entrada)){
             0
         } else if (VCARD_PATTERN.containsMatchIn(entrada)) {
             1
         } else if (MAIL_PATTERN.containsMatchIn(entrada) || MAIL_PATTERN2.containsMatchIn(entrada)){
             2
         } else if (WEB_PATTERN.containsMatchIn(entrada)){
             3
         } else {
             -1
         }
    }

}