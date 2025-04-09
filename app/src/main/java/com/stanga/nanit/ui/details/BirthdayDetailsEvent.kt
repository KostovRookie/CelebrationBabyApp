package com.stanga.nanit.ui.details

sealed class BirthdayDetailsEvent {
    data class NameChanged(val name: String) : BirthdayDetailsEvent()
    data class BirthdayChanged(val birthday: Long) : BirthdayDetailsEvent()
    data class PictureChanged(val path: String) : BirthdayDetailsEvent()
    object SaveBirthday : BirthdayDetailsEvent()
}