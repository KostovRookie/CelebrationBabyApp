package com.stanga.nanit

import app.cash.turbine.test
import com.stanga.nanit.data.BirthdayEntity
import com.stanga.nanit.data.DatabaseDao
import com.stanga.nanit.domain.manager.DataStoreManager
import com.stanga.nanit.ui.details.BirthdayDetailsEvent
import com.stanga.nanit.ui.details.BirthdayViewModel

import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class BirthdayViewModelTest {

    private lateinit var dao: DatabaseDao
    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var viewModel: BirthdayViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        dao = mockk(relaxed = true)
        dataStoreManager = mockk(relaxed = true)

        coEvery { dao.getKid() } returns null
        coEvery { dataStoreManager.getPictureUri() } returns flowOf(null)

        viewModel = BirthdayViewModel(dao, dataStoreManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty`() = runTest {
        viewModel.state.test {
            val firstState = awaitItem()
            assertEquals("", firstState.name)
            assertEquals(null, firstState.birthday)
            assertEquals(null, firstState.pictureUri)
            assertEquals(false, firstState.isButtonEnabled)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onEvent NameChanged updates name and button enabled`() = runTest {
        viewModel.onEvent(BirthdayDetailsEvent.NameChanged("Alex"))

        viewModel.state.test {
            val updatedState = awaitItem()
            assertEquals("Alex", updatedState.name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onEvent BirthdayChanged updates birthday and button enabled`() = runTest {
        viewModel.onEvent(BirthdayDetailsEvent.NameChanged("Alex"))
        viewModel.onEvent(BirthdayDetailsEvent.BirthdayChanged(123456789L))

        viewModel.state.test {
            val updatedState = awaitItem()
            assertEquals(123456789L, updatedState.birthday)
            assertEquals(true, updatedState.isButtonEnabled)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onEvent PictureChanged updates pictureUri and calls save`() = runTest {
        val path = "fake/path/to/image.jpg"

        viewModel.onEvent(BirthdayDetailsEvent.PictureChanged(path))

        viewModel.state.test {
            val updatedState = awaitItem()
            assertEquals(path, updatedState.pictureUri)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { dataStoreManager.savePictureUri(path) }
        coVerify { dao.saveKid(any()) }
    }

    @Test
    fun `onEvent SaveBirthday calls saveKid`() = runTest {
        viewModel.onEvent(BirthdayDetailsEvent.SaveBirthday)

        coVerify { dao.saveKid(any()) }
    }
}