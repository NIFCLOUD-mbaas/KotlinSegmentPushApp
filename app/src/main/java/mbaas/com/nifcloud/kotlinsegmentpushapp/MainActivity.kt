package mbaas.com.nifcloud.kotlinsegmentpushapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.nifcloud.mbaas.core.NCMB
import com.nifcloud.mbaas.core.NCMBCallback
import com.nifcloud.mbaas.core.NCMBInstallation
import com.nifcloud.mbaas.core.NCMBQuery
import org.json.JSONArray
import org.json.JSONException


class MainActivity : AppCompatActivity() {

    lateinit var _objectId: TextView
    lateinit var _appversion: TextView
    lateinit var _channels: Spinner
    lateinit var _devicetoken: TextView
    lateinit var _sdkversion: TextView
    lateinit var _timezone: TextView
    lateinit var _createdate: TextView
    lateinit var _updatedate: TextView
    lateinit var _txtPrefectures: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //**************** APIキーの設定とSDKの初期化 **********************
        NCMB.initialize(
            this,
            "YOUR_APPLICATION_KEY",
            "YOUR_CLIENT_KEY"
        )
        NCMB.initializePush(this)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= 33) {
            askNotificationPermission()
        }

        //表示する端末情報のデータを反映
        _objectId = findViewById<View>(R.id.txtObject) as TextView
        _appversion = findViewById<View>(R.id.txtAppversion) as TextView
        _channels = findViewById<View>(R.id.spinChannel) as Spinner
        _devicetoken = findViewById<View>(R.id.txtDevicetoken) as TextView
        _sdkversion = findViewById<View>(R.id.txtSdkversion) as TextView
        _timezone = findViewById<View>(R.id.txtTimezone) as TextView
        _createdate = findViewById<View>(R.id.txtCreatedate) as TextView
        _updatedate = findViewById<View>(R.id.txtUpdatedate) as TextView
        _txtPrefectures = findViewById<View>(R.id.txtPrefecture) as EditText

        var installation = NCMBInstallation.currentInstallation
        installation.getDeviceTokenInBackground(NCMBCallback { e, token ->
            if (e != null) {
                //保存に失敗した場合の処理
                Log.d("error", "保存に失敗しました : " + e.message)
            } else {
                //保存に成功した場合の処理
                val query = NCMBQuery.forInstallation()
                query.whereEqualTo("deviceToken", token as String)
                query.findInBackground(NCMBCallback { e, objects ->
                    if (e != null) {
                        //エラー時の処理
                        println("検索に失敗しました。エラー:" + e.message)
                    } else {
                        objects as List<NCMBInstallation>
                        installation = objects[0]
                        runOnUiThread {
                            _objectId.text = installation.getObjectId()
                            _devicetoken.text = token
                            _appversion.text = installation.appVersion
                            try {
                                if (installation.channels != null) {
                                    val selectChannel = installation.channels
                                    selectChannel as JSONArray
                                    val channelArray = arrayOf("A", "B", "C", "D")
                                    val selectId = channelArray.indexOf(selectChannel[0].toString())
                                    _channels.setSelection(selectId)
                                }
                            } catch (e2: JSONException) {
                                e2.printStackTrace()
                            }
                            _sdkversion.text = installation.sdkVersion
                            _timezone.text = installation.timeZone
                            _createdate.text = installation.getCreateDate().toString()
                            _updatedate.text = installation.getUpdateDate().toString()
                            if (installation.getString("Prefectures") != null) {
                                _txtPrefectures.setText(installation.getString("Prefectures"))
                            }
                        }
                    }
                })
            }
        })

        val _btnSave = findViewById<View>(R.id.btnSave) as Button
        _btnSave.setOnClickListener {
            _channels = findViewById<View>(R.id.spinChannel) as Spinner
            _txtPrefectures = findViewById<View>(R.id.txtPrefecture) as EditText
            val prefectures = _txtPrefectures.text.toString()
            val item = _channels.selectedItem as String
            val tmpArray = JSONArray()
            tmpArray.put(item)
            installation.channels = tmpArray
            installation.put("Prefectures", prefectures)
            installation.saveInBackground(NCMBCallback { e, ncmbObj ->
                runOnUiThread {
                    if (e != null) {
                        Toast.makeText(
                            this,
                            "端末情報の保存に失敗しました。" + e.message,
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(this, "端末情報の保存に成功しました。", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            })
        }

    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }
    private fun askNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            // FCM SDK (and your app) can post notifications.
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
            // TODO: display an educational UI explaining to the user the features that will be enabled
            //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
            //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
            //       If the user selects "No thanks," allow the user to continue without notifications.
        } else {
            // Directly ask for the permission
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
