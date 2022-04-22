package mbaas.com.nifcloud.kotlinsegmentpushapp

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
}