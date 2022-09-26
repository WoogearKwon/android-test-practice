package eu.maxkim.boredombuster.activity.ui.newactivity

import eu.maxkim.boredombuster.activity.framework.api.ActivityApiClient
import eu.maxkim.boredombuster.activity.framework.datasource.ActivityLocalDataSourceImpl
import eu.maxkim.boredombuster.activity.framework.datasource.ActivityRemoteDataSourceImpl
import eu.maxkim.boredombuster.activity.framework.db.ActivityDao
import eu.maxkim.boredombuster.activity.repository.ActivityRepositoryImpl
import eu.maxkim.boredombuster.activity.usecase.DeleteActivityImpl
import eu.maxkim.boredombuster.activity.usecase.GetRandomActivityImpl
import eu.maxkim.boredombuster.activity.usecase.IsActivitySavedImpl
import eu.maxkim.boredombuster.activity.usecase.SaveActivityImpl
import eu.maxkim.boredombuster.util.CoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.times
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class NewActivityViewModelIntegrationTest {

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)
    private val testScope = TestScope(testDispatcher)

    @get:Rule
    val coroutineRule = CoroutineRule(testDispatcher)

    private val mockApiClient: ActivityApiClient = mock()
    private val mockActivityDao: ActivityDao = mock()

    private val remoteDataSource = ActivityRemoteDataSourceImpl(mockApiClient)
    private val localDataSource = ActivityLocalDataSourceImpl(mockActivityDao)

    private val activityRepository = ActivityRepositoryImpl(
        appScope = testScope,
        ioDispatcher = testDispatcher,
        remoteDataSource = remoteDataSource,
        localDataSource = localDataSource
    )

    private val getRandomActivity = GetRandomActivityImpl(activityRepository)
    private val saveActivity = SaveActivityImpl(activityRepository)
    private val deleteActivity = DeleteActivityImpl(activityRepository)
    private val isActivitySaved = IsActivitySavedImpl(activityRepository)


    @Test
    fun `loadNewActivity()를 호출하면 api client가 트리거된다`() = runTest {
        // Arrange
        val viewModel = NewActivityViewModel(
            getRandomActivity,
            saveActivity,
            deleteActivity,
            isActivitySaved
        )

        // Act
        viewModel.loadNewActivity()
        runCurrent()

        // Assert
        verify(mockApiClient, times(1)).getActivity()
    }
}