package eu.maxkim.boredombuster.activity.ui.newactivity

import eu.maxkim.boredombuster.activity.fake.usecase.*
import eu.maxkim.boredombuster.util.CoroutineRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NewActivityViewModelTest {

    @get:Rule
    val coroutineRule = CoroutineRule()

    @Test
    fun `creating a viewModel exposes loading ui state`() {
        // Arrange
        val viewModel = NewActivityViewModel(
            FakeGetRandomActivity(),
            FakeSaveActivity(),
            FakeDeleteActivity(),
            FakeIsActivitySaved()
        )

        // Assert
        assert(viewModel.uiState.value is NewActivityUiState.Loading)
    }

    @Test
    fun `creating a viewModel updates uui state to success after loading`() {
        // Arrange
        val viewModel = NewActivityViewModel(
            FakeGetRandomActivity(),
            FakeSaveActivity(),
            FakeDeleteActivity(),
            FakeIsActivitySaved()
        )

        val expectedUiState = NewActivityUiState.Success(activity1, false)

        // Act
        coroutineRule.testDispatcher.scheduler.runCurrent()

        // Assert
        val actualState = viewModel.uiState.value
        assertEquals(actualState, expectedUiState)
    }

    @Test
    fun `creating a viewModel updates ui state to error in case of failure`() {
        // Arrange
        val viewModel = NewActivityViewModel(
            FakeGetRandomActivity(isSuccessful = false),
            FakeSaveActivity(),
            FakeDeleteActivity(),
            FakeIsActivitySaved()
        )

        // Act
        coroutineRule.testDispatcher.scheduler.runCurrent()

        // Assert
        val currentState = viewModel.uiState.value
        assert(currentState is NewActivityUiState.Error)
    }

    @Test
    fun `if activity is already saved, ui state's isFavorite is ste to true`() {
        // Arrange
        val viewModel = NewActivityViewModel(
            FakeGetRandomActivity(),
            FakeSaveActivity(),
            FakeDeleteActivity(),
            FakeIsActivitySaved(isActivitySaved = true)
        )

        val expectedUiState = NewActivityUiState.Success(activity1, true)

        // Act
        coroutineRule.testDispatcher.scheduler.runCurrent()

        // Assert
        val actualState = viewModel.uiState.value
        assertEquals(actualState, expectedUiState)
    }

    @Test
    fun `calling loadNewActivity() updates ui state with a new activity`() {
        // Arrange
        val fakeGetRandomActivity = FakeGetRandomActivity()
        val viewModel = NewActivityViewModel(
            fakeGetRandomActivity,
            FakeSaveActivity(),
            FakeDeleteActivity(),
            FakeIsActivitySaved()
        )

        // this can be omitted, but it is nice to not have any pending tasks
        coroutineRule.testDispatcher.scheduler.runCurrent()

        val expectedUiState = NewActivityUiState.Success(activity2, false)
        fakeGetRandomActivity.activity = activity2

        // Act
        viewModel.loadNewActivity()
        coroutineRule.testDispatcher.scheduler.runCurrent()

        // Assert
        val actualState = viewModel.uiState.value
        assertEquals(actualState, expectedUiState)
    }
}