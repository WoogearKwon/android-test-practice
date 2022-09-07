package eu.maxkim.boredombuster.activity.ui.favorite

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import eu.maxkim.boredombuster.activity.fake.usecase.activity1
import eu.maxkim.boredombuster.activity.fake.usecase.activity2
import eu.maxkim.boredombuster.activity.model.Activity
import eu.maxkim.boredombuster.activity.usecase.DeleteActivity
import eu.maxkim.boredombuster.activity.usecase.GetFavoriteActivities
import eu.maxkim.boredombuster.util.CoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
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

    @get:Rule
    val coroutineRule = CoroutineRule()

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

        // stubbing liveData in viewModel
        whenever(mockGetFavoriteActivities.invoke()).doReturn(liveDataToReturn)

        val viewModel = FavoritesViewModel(
            mockGetFavoriteActivities,
            mockDeleteActivity
        )

        // Act
        // liveData 를 통해 전달된 데이터를 어딘가에 잡아두고(capture), 나중에 assert 할 때 사용해야 한다.
        // we are using observeForever since we don't have [LifeCycleOwner]
        viewModel.uiStateLiveData.observeForever(activityListObserver)

        // Assert
        // captor: 모의 객체를 호출할 때 인자를 검증하기 위함
        // (메서드 호출 여부를 검증하는 과정에서 실제 호출 시 전달한 인자를 보관할 수 있음)
        verify(activityListObserver, times(1)).onChanged(activityListCaptor.capture())
        assert(activityListCaptor.value is FavoritesUiState.List)

        val actualList = (activityListCaptor.value as FavoritesUiState.List).activityList
        assertEquals(actualList, expectedList)
    }

    @Test
    fun `the viewModel maps empty list of activities to empty ui state`() {
        // Arrange
        val liveDataToReturn = MutableLiveData<List<Activity>>()
            .apply { value = listOf() }

        whenever(mockGetFavoriteActivities.invoke()).doReturn(liveDataToReturn)

        val viewModel = FavoritesViewModel(
            mockGetFavoriteActivities,
            mockDeleteActivity
        )

        // Act
        viewModel.uiStateLiveData.observeForever(activityListObserver)

        // Assert
        verify(activityListObserver, times(1)).onChanged(activityListCaptor.capture())
        assert(activityListCaptor.value is FavoritesUiState.Empty)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `calling deleteActivity() interacts with the correct use case`() = runTest {
        // Arrange
        val viewModel = FavoritesViewModel(
            mockGetFavoriteActivities,
            mockDeleteActivity
        )

        // Act
        viewModel.deleteActivity(activity1)
        advanceUntilIdle() // works the same as runCurrent() in this case

        // Assert
        verify(mockDeleteActivity, times(1)).invoke(activity1)
    }
}