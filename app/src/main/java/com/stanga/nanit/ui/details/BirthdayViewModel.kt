package com.stanga.nanit.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stanga.nanit.data.DatabaseDao
import com.stanga.nanit.data.KidEntity
import com.stanga.nanit.domain.manager.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BirthdayViewModel(
    private val dao: DatabaseDao,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _state = MutableStateFlow(BirthdayDetailsState())
    val state: StateFlow<BirthdayDetailsState> = _state

    init {
        loadKid()
    }

    private fun loadKid() {
        viewModelScope.launch {
            val kid = dao.getKid()
            if (kid != null) {
                _state.value = _state.value.copy(
                    name = kid.name,
                    birthday = kid.birthday,
                    pictureUri = kid.pictureUri,
                    isButtonEnabled = kid.name.isNotBlank() && kid.birthday > 0
                )
            } else {
                dataStoreManager.getPictureUri().collect { uri ->
                    _state.update { it.copy(pictureUri = uri) }
                }
            }
        }
    }

    fun onEvent(event: BirthdayDetailsEvent) {
        when (event) {
            is BirthdayDetailsEvent.NameChanged -> {
                _state.update {
                    it.copy(
                        name = event.name,
                        isButtonEnabled = event.name.isNotBlank() && _state.value.birthday != null
                    )
                }
            }

            is BirthdayDetailsEvent.BirthdayChanged -> {
                _state.update {
                    it.copy(
                        birthday = event.birthday,
                        isButtonEnabled = _state.value.name.isNotBlank() && event.birthday > 0
                    )
                }
            }

            is BirthdayDetailsEvent.PictureChanged -> {
                _state.update { it.copy(pictureUri = event.path) }
                savePictureUri(event.path)
                saveKid()
            }

            is BirthdayDetailsEvent.SaveBirthday -> {
                saveKid()
            }
        }
    }

    private fun saveKid() {
        viewModelScope.launch {
            val kid = KidEntity(
                name = _state.value.name,
                birthday = _state.value.birthday ?: 0L,
                pictureUri = _state.value.pictureUri
            )
            dao.saveKid(kid)
        }
    }

    private fun savePictureUri(path: String) {
        viewModelScope.launch {
            dataStoreManager.savePictureUri(path)
        }
    }
}