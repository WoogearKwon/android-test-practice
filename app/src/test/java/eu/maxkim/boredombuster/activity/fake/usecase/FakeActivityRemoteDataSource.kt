package eu.maxkim.boredombuster.activity.fake.usecase

import eu.maxkim.boredombuster.activity.model.Activity
import eu.maxkim.boredombuster.activity.repository.ActivityRemoteDataSource
import eu.maxkim.boredombuster.model.Result

class FakeActivityRemoteDataSource : ActivityRemoteDataSource {

    var getActivityWasCancelled = false
        private set

    override suspend fun getActivity(): Result<Activity> {
        getActivityWasCancelled = true
        return Result.Success(activity1)
    }
}