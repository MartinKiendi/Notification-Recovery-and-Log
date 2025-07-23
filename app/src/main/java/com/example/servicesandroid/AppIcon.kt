package com.example.servicesandroid

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.example.servicesandroid.ServiceApplication.Companion.myPackageManager
import org.apache.commons.lang3.concurrent.Computable
import org.apache.commons.lang3.concurrent.Memoizer

internal object AppIconComputable : Computable<String, Drawable?> {
    override fun compute(packageName: String): Drawable? {
        return try {
            myPackageManager.getApplicationIcon(packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }
}

object AppIcon : Memoizer<String, Drawable?>(AppIconComputable, true)

