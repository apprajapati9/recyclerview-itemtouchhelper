package ca.apprajapati.recyclerview.util

import android.content.Context
import android.graphics.Color
import kotlin.random.Random

class UiUtil {

    companion object {

        fun dpToPx(
            context: Context,
            dp : Float) : Int{
            return Math.round(dp * context.resources.displayMetrics.density)

        }

        fun generateRandomColor(): Int {
            val red = Random.nextInt(256)   // Random value between 0 and 255
            val green = Random.nextInt(256) // Random value between 0 and 255
            val blue = Random.nextInt(256)  // Random value between 0 and 255
            val alpha = Random.nextInt(256)

            return Color.argb(alpha,red, green, blue)  // Returns a color in RGBA format
        }
    }
}
