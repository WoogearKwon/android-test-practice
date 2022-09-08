package eu.maxkim.boredombuster.activity.framework.datasource

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import eu.maxkim.boredombuster.activity.fake.usecase.activity1
import eu.maxkim.boredombuster.activity.framework.db.ActivityDao
import eu.maxkim.boredombuster.framework.AppDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ActivityLocalDataSourceImplTest {
    private lateinit var activityDao: ActivityDao
    private lateinit var database: AppDatabase

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
}