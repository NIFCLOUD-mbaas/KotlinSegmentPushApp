package mbaas.com.nifcloud.kotlinsegmentpushapp

import android.os.Build
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector
import com.nifcloud.mbaas.core.NCMBException
import com.nifcloud.mbaas.core.NCMBPush

class Utils {
    companion object {
        const val NOTIFICATION_TITLE = "UITest push notification"
        const val NOTIFICATION_TEXT =
            "Thank you! We appreciate your business, and we’ll do our best to continue to give you the kind of service you deserve."
    }

    fun sendPush() {
        val push = NCMBPush()
        push.title = NOTIFICATION_TITLE
        push.message = NOTIFICATION_TEXT
        push.immediateDeliveryFlag = true
        //Androidに送る場合
        push.isSendToAndroid = true
        try {
            push.save()
        }
        catch (error: NCMBException){
            throw error
        }
    }

    internal fun allowPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= 33) {
            val device = UiDevice.getInstance(getInstrumentation())
            val allowPermissions = device.findObject(UiSelector().text("Allow"))
            if (allowPermissions.exists()) {
                try {
                    allowPermissions.click()
                } catch (e: UiObjectNotFoundException) {
                    Log.d("NCMBTest", "Error: " + e.message)
                }
            }
        }
    }
}