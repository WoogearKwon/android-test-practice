package eu.maxkim.boredombuster.activity.repository

import eu.maxkim.boredombuster.activity.fake.usecase.FakeActivityLocalDataSource
import eu.maxkim.boredombuster.activity.fake.usecase.FakeActivityRemoteDataSource
import eu.maxkim.boredombuster.activity.fake.usecase.activity1
import eu.maxkim.boredombuster.model.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ActivityRepositoryImplTest {

    @Test
    fun `context가 전환된 후에 getNewActivity()가 result를 반환한다 `() = runTest {
        // Arrange
        val activityRepository = ActivityRepositoryImpl(
            appScope = this,
            ioDispatcher = StandardTestDispatcher(testScheduler),
            remoteDataSource = FakeActivityRemoteDataSource(),
            localDataSource = FakeActivityLocalDataSource()
        )

        val expectedActivity = activity1

        // Act
        val result = activityRepository.getNewActivity()

        // Assert
        assert(result is Result.Success)
        assertEquals((result as Result.Success).data, expectedActivity)
    }

    @Test
    fun `getNewActivityInANewCoroutine() remote data source를 정확하게 호출한다`() = runTest {
        // Arrange
        val fakeRemoteRepository = FakeActivityRemoteDataSource()
        val activityRepository = ActivityRepositoryImpl(
            appScope = this,
            ioDispatcher = StandardTestDispatcher(testScheduler),
            remoteDataSource = fakeRemoteRepository,
            localDataSource = FakeActivityLocalDataSource()
        )

        // Act
        activityRepository.getNewActivityInANewCoroutine()
        advanceUntilIdle()

        // Assert
        assert(fakeRemoteRepository.getActivityWasCancelled)
    }
}