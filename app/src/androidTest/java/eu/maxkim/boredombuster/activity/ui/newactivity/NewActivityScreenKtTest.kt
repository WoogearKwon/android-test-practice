package eu.maxkim.boredombuster.activity.ui.newactivity

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import eu.maxkim.boredombuster.R
import eu.maxkim.boredombuster.Tags
import eu.maxkim.boredombuster.activity.fake.usecase.activity1
import org.junit.Rule
import org.junit.Test
import org.mockito.internal.verification.VerificationModeFactory.times
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class NewActivityScreenKtTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun activityNameDisplayedOnACard() {
        composeTestRule.setContent {
            NewActivityCard(
                modifier = Modifier.fillMaxWidth(),
                activity = activity1,
                isFavorite = false,
                onFavoriteClick = { },
                onLinkClick = { }
            )
        }

        composeTestRule.onNodeWithText(activity1.name)
            .assertIsDisplayed()
    }

    @Test
    fun onFavoriteClickCallbackIsTriggered() {
        val onFavoriteClick: (isFavorite: Boolean) -> Unit = mock()
        val isFavorite = false

        composeTestRule.setContent {
            NewActivityCard(
                modifier = Modifier.fillMaxWidth(),
                activity = activity1,
                isFavorite = isFavorite,
                onFavoriteClick = onFavoriteClick,
                onLinkClick = { }
            )
        }

        val contentDescription = ApplicationProvider.getApplicationContext<Context>()
            .getString(R.string.cd_save_activity)

        composeTestRule.onNodeWithContentDescription(contentDescription)
            .performClick()

        verify(onFavoriteClick, times(1)).invoke(!isFavorite)
    }

    @Test
    fun onLinkClickCallbackIsTriggered() {
        val onLinkClick: (link: String) -> Unit = mock()

        composeTestRule.setContent {
            NewActivityCard(
                modifier = Modifier.fillMaxWidth(),
                activity = activity1,
                isFavorite = false,
                onFavoriteClick = {},
                onLinkClick = onLinkClick
            )
        }

        composeTestRule.onNodeWithTag(Tags.ActivityLink)
            .performClick()

        verify(onLinkClick, times(1)).invoke(activity1.link)
    }
}