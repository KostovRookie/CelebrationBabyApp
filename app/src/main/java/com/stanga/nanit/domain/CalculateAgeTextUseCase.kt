package com.stanga.nanit.domain

import java.util.concurrent.TimeUnit

class CalculateAgeTextUseCase {

    fun execute(birthdayMillis: Long?): Pair<String, String> {
        if (birthdayMillis == null) return "1" to "MONTH OLD!"

        val now = System.currentTimeMillis()
        val diffMillis = now - birthdayMillis
        val days = TimeUnit.MILLISECONDS.toDays(diffMillis)

        if (days < 30) {
            return "1" to "MONTH OLD!"
        }

        val months = (days / 30).toInt()
        return if (months < 12) {
            months.toString() to if (months == 1) "MONTH OLD!" else "MONTHS OLD!"
        } else {
            val years = months / 12
            years.toString() to if (years == 1) "YEAR OLD!" else "YEARS OLD!"
        }
    }
}