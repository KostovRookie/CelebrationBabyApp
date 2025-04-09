package com.stanga.nanit.ui.details

data class BirthdayDetailsState(
    val name: String = "",
    val birthday: Long? = null,
    val pictureUri: String? = null,
    val isButtonEnabled: Boolean = false
)