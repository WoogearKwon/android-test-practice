package eu.maxkim.boredombuster.activity.fake.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import eu.maxkim.boredombuster.activity.model.Activity
import eu.maxkim.boredombuster.activity.repository.ActivityLocalDataSource

class FakeActivityLocalDataSource : ActivityLocalDataSource {

    override suspend fun saveActivity(activity: Activity) {
        // save
    }

    override suspend fun deleteActivity(activity: Activity) {
        // delete
    }

    override suspend fun isActivitySaved(key: String): Boolean {
        return false
    }

    override fun getActivityListLiveData(): LiveData<List<Activity>> {
        return MutableLiveData()
    }

}