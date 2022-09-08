package eu.maxkim.boredombuster.activity.framework.datasource

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import eu.maxkim.boredombuster.activity.fake.usecase.activity1
import eu.maxkim.boredombuster.activity.fake.usecase.activity2
import eu.maxkim.boredombuster.activity.framework.db.ActivityDao
import eu.maxkim.boredombuster.activity.model.Activity
import eu.maxkim.boredombuster.framework.AppDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito.times
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class ActivityLocalDataSourceImplTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var activityDao: ActivityDao
    private lateinit var database: AppDatabase

    private val activityListObserver: Observer<List<Activity>> = mock()

    @Captor
    private lateinit var activityListCaptor: ArgumentCaptor<List<Activity>>

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        activityDao = database.activityDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun can_save_activity_to_the_db_and_read_it() = runTest{
        // Arrange
        val activityLocalDataSource = ActivityLocalDataSourceImpl(activityDao)

        // Act
        activityLocalDataSource.saveActivity(activity1)

        // Assert
        assert(activityLocalDataSource.isActivitySaved(activity1.key))
    }

    @Test
    fun can_delete_activity_from_db() = runTest {
        // Arrange
        val activityLocalSource = ActivityLocalDataSourceImpl(activityDao)
        activityLocalSource.saveActivity(activity1)

        // Act
        activityLocalSource.deleteActivity(activity1)

        // Assert
        assert(activityLocalSource.isActivitySaved(activity1.key).not())
    }

    @Test
    fun can_save_activity_to_the_db_and_observe_the_livedata() = runTest {
        // Arrange
        val activityLocalDataSource = ActivityLocalDataSourceImpl(activityDao)
        val expectedList = listOf(activity1, activity2)

        // Act
        activityLocalDataSource.getActivityListLiveData()
            .observeForever(activityListObserver)
        activityLocalDataSource.saveActivity(activity1)
        activityLocalDataSource.saveActivity(activity2)

        // Assert
        /*
        * In this test, we start observing the database when there are no entries.
        * Therefore, our first onChanged event will fire with an empty list.
        * We expect our LiveData to be updated after every save,
        * hence onChanged should be triggered three times.
        * And the final list should contain both activities.
        * */
        verify(activityListObserver, times(3))
            .onChanged(activityListCaptor.capture())
        assertEquals(activityListCaptor.value, expectedList)
    }
}