package ca.apprajapati.recyclerview.util

import android.content.Context

class UiUtil {

    companion object {

        fun dpToPx(
            context: Context,
            dp : Float) : Int{
            return Math.round(dp * context.resources.displayMetrics.density)

        }
    }
}
