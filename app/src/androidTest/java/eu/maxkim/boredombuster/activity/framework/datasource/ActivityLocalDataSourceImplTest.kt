package eu.maxkim.boredombuster.activity.framework.datasource

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import eu.maxkim.boredombuster.activity.framework.db.ActivityDao
import eu.maxkim.boredombuster.framework.AppDatabase
import org.junit.After
import org.junit.Assert.*
import org.junit.Before

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
}