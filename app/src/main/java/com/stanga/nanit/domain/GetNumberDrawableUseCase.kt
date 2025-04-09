package com.stanga.nanit.domain

import com.stanga.nanit.R

class GetNumberDrawableUseCase {

    fun execute(age: Int): Int {
        return when (age) {
            1 -> R.drawable.ic_one
            2 -> R.drawable.ic_two
            3 -> R.drawable.ic_three
            4 -> R.drawable.ic_four
            5 -> R.drawable.ic_five
            6 -> R.drawable.ic_six
            7 -> R.drawable.ic_seven
            8 -> R.drawable.ic_eight
            9 -> R.drawable.ic_nine
            10 -> R.drawable.ic_ten
            11 -> R.drawable.ic_eleven
            12 -> R.drawable.ic_twelve
            else -> R.drawable.ic_one
        }
    }
}