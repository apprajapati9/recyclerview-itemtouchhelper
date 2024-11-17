package ca.apprajapati.recyclerview.util

import android.content.Context
import android.graphics.Color
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.google.android.material.color.MaterialColors

class ResourceUtil {

    companion object {

        /*
            @ColorRes - gets a specific resource mentioned in colors.xml, static value.
         */
        fun getColor(context: Context, @ColorRes colorId : Int) : Int =
            ContextCompat.getColor(context, colorId)

        /*
            @AttrRes - if you want value to change and adapt based on the current theme,
                     ?attr/colorPrimary
         */
        fun getColorAttrs(context: Context, @AttrRes colorId : Int) : Int =
                    MaterialColors.getColor(context, colorId, Color.BLACK)


    }

}