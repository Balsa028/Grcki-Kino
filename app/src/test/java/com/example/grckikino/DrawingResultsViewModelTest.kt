package com.example.grckikino

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.grckikino.api.Result
import com.example.grckikino.models.DrawingResults
import com.example.grckikino.models.ResultAdapterItem
import com.example.grckikino.models.ResultItem
import com.example.grckikino.models.WinningNumbers
import com.example.grckikino.repository.DrawingsRepository
import com.example.grckikino.utils.GRCKI_LOTO_GAME_ID
import com.example.grckikino.utils.HEADER_TYPE
import com.example.grckikino.utils.RESULT_ITEM_TYPE
import com.example.grckikino.viewmodels.DrawingResultViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
class DrawingResultsViewModelTest {

    @get:Rule
    val instantExecutorRule: TestRule = InstantTaskExecutorRule()

    private lateinit var viewModel: DrawingResultViewModel

    private val repository: DrawingsRepository = mockk()

    private val observer: Observer<Result<DrawingResults>> = mockk(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        viewModel = DrawingResultViewModel(repository)
        Dispatchers.setMain(testDispatcher)
        viewModel.resultsListResponse.observeForever(observer)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        viewModel.resultsListResponse.removeObserver(observer)
    }

    @Test
    fun `get drawings result emits loading state first`() = runTest {
        coEvery { repository.getDrawingResults(GRCKI_LOTO_GAME_ID, FROM_DATE, TO_DATE) } returns Result.Loading()
        viewModel.getDrawingResults(FROM_DATE, TO_DATE)

        verify { observer.onChanged(any<Result.Loading<DrawingResults>>()) }
    }

    @Test
    fun `get drawings result emits success state after fetching`() = runTest {
        val resultItems = listOf(
            ResultItem(drawTime = 1122334, drawId = 1, winningNumbers = WinningNumbers(listOf(1, 2, 3))),
            ResultItem(drawTime = 1122334, drawId = 2, winningNumbers = WinningNumbers(listOf(4, 5, 6)))
        )

        val drawingResults = DrawingResults(resultItems)
        val successResult = Result.Success(drawingResults)

        coEvery { repository.getDrawingResults(GRCKI_LOTO_GAME_ID, FROM_DATE, TO_DATE) } returns successResult

        viewModel.getDrawingResults(FROM_DATE, TO_DATE)
        advanceUntilIdle()

        verify { observer.onChanged(successResult) }
    }

    @Test
    fun `get drawings result emits error state on failure`() = runTest {
        val errorMessage = "Error fetching results"
        val errorResult = Result.Error<DrawingResults>(errorMessage)

        coEvery { repository.getDrawingResults(GRCKI_LOTO_GAME_ID, FROM_DATE, TO_DATE) } returns errorResult

        viewModel.getDrawingResults(FROM_DATE, TO_DATE)
        advanceUntilIdle()

        coVerify { observer.onChanged(errorResult) }
    }


    @Test
    fun `prepareResultItems correctly formats result items`() {
        val resultList = listOf(
            ResultItem(drawTime = 1725869366, drawId = 1, winningNumbers = WinningNumbers(listOf(1, 2, 3))),
            ResultItem(drawTime = 1725869366, drawId = 2, winningNumbers = WinningNumbers(listOf(4, 5, 6)))
        )
        val expectedAdapterItems = listOf(
            ResultAdapterItem(type = HEADER_TYPE, drawTime = "09.09   00:24", drawId = 1),
            ResultAdapterItem(type = RESULT_ITEM_TYPE, winningNumbers = listOf(1, 2, 3)),
            ResultAdapterItem(type = HEADER_TYPE, drawTime = "09.09   00:24", drawId = 2),
            ResultAdapterItem(type = RESULT_ITEM_TYPE, winningNumbers = listOf(4, 5, 6))
        )

        val resultAdapterItems = viewModel.prepareResultItems(resultList)

        assertEquals(expectedAdapterItems, resultAdapterItems)
    }

    companion object {
        private const val FROM_DATE = "2024-01-01"
        private const val TO_DATE = "2024-01-02"
    }
 }