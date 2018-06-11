package com.muei.apm.runtrack.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

class SocialMediaUtils {
    companion object {
        fun getTwitterIntent(ctx: Context, shareText: String): Intent {
            val tweetUrl = "https://twitter.com/intent/tweet?text=$shareText"
            val uri = Uri.parse(tweetUrl)
            return Intent(Intent.ACTION_VIEW, uri)
        }
    }
}