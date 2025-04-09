package com.stanga.nanit.di

import androidx.room.Room
import com.stanga.nanit.data.NanitDatabase
import com.stanga.nanit.domain.manager.DataStoreManager
import com.stanga.nanit.ui.details.BirthdayViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single {
        Room.databaseBuilder(
            get(),
            NanitDatabase::class.java,
            "kids_db"
        ).build()
    }

    single { get<NanitDatabase>().kidDao() }

    single { DataStoreManager(get()) }

    viewModel { BirthdayViewModel(get(), get()) }
}