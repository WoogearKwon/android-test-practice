package eu.maxkim.boredombuster.activity.fake.usecase

import eu.maxkim.boredombuster.activity.model.Activity
import eu.maxkim.boredombuster.activity.usecase.GetRandomActivity
import eu.maxkim.boredombuster.model.Result
import java.lang.RuntimeException

class FakeGetRandomActivity(
    private val isSuccessful: Boolean = true
) : GetRandomActivity {

    var activity: Activity? = null

    override suspend fun invoke(): Result<Activity> {
        return if (isSuccessful) {
            Result.Success(activity ?: activity1)
        } else {
            Result.Error(RuntimeException("Boom..."))
        }
    }
}