package eu.maxkim.boredombuster.activity.ui.favorite

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import eu.maxkim.boredombuster.activity.fake.usecase.activity1
import eu.maxkim.boredombuster.activity.fake.usecase.activity2
import eu.maxkim.boredombuster.activity.model.Activity
import eu.maxkim.boredombuster.activity.usecase.DeleteActivity
import eu.maxkim.boredombuster.activity.usecase.GetFavoriteActivities
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*

@RunWith(MockitoJUnitRunner::class)
class FavoritesViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    // JUnit will create a new test class instance each time it runs a test,
    // so we don't need to reset mocks after each test to avoid a dirty state.
    private val mockGetFavoriteActivities: GetFavoriteActivities = mock()
    private val mockDeleteActivity: DeleteActivity = mock()

    private val activityListObserver: Observer<FavoritesUiState> = mock()

    @Captor
    private lateinit var activityListCaptor: ArgumentCaptor<FavoritesUiState>

    @Test
    fun `the viewModel maps list of activities to list ui state`() {
        // Arrange
        val liveDataToReturn = MutableLiveData<List<Activity>>()
            .apply { value = listOf(activity1, activity2) }

        val expectedList = listOf(activity1, activity2)

        whenever(mockGetFavoriteActivities.invoke()).doReturn(liveDataToReturn)

        val viewModel = FavoritesViewModel(
            mockGetFavoriteActivities,
            mockDeleteActivity
        )

        // Act
        viewModel.uiStateLiveData.observeForever(activityListObserver)

        // Assert
        verify(activityListObserver, times(1)).onChanged(activityListCaptor.capture())
        assert(activityListCaptor.value is FavoritesUiState.List)

        val actualList = (activityListCaptor.value as FavoritesUiState.List).activityList
        assertEquals(actualList, expectedList)
    }
}