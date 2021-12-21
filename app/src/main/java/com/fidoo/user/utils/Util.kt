@file:JvmName("UtilClass")

package com.fidoo.user.utils

/*import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.yarolegovich.lovelydialog.LovelyInfoDialog
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch*/

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Paint
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Html
import android.text.Selection
import android.text.format.DateFormat
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.fidoo.user.R
import com.fidoo.user.activity.AuthActivity
import com.fidoo.user.activity.SplashActivity
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


const val TRADEAPP_PICK_IMAGE_FROM_GALLARY = 111
const val TRADEAPP_PICK_VIDEO_FROM_GALLARY = 222
const val TRADEAPP_PICK_AUDIO_FROM_GALLARY = 333
const val TRADEAPP_PICK_FILE__FROM_GALLARY = 444

const val AUTOCOMPLETE_REQUEST_CODE = 1
const val FORADDRESS_REQUEST_CODE = 101
const val MY_PERMISSIONS_REQUEST_CODE = 123
const val PERMISSIONS_REQUEST_READ_CONTACTS = 100
const val PICK_CONTACT_REQUEST = 10001


fun getYears(): ArrayList<String> {
    val years = ArrayList<String>()

    val thisYear = Calendar.getInstance().get(Calendar.YEAR)

    for (i in thisYear..thisYear + 20) {
        years.add(Integer.toString(i))
    }

    return years

}

fun getDestructorTimingData() = arrayOf(
    "Off",
    "1 second -Disabled",
    "2 seconds -Disabled",
    "3 seconds",
    "4 seconds",
    "5 seconds",
    "6 seconds",
    "7 seconds"
    ,
    "8 seconds",
    "9 seconds",
    "10 seconds",
    "11 seconds",
    "12 seconds",
    "13 seconds",
    "14 seconds",
    "15 seconds",
    "30 seconds",
    "1 minute",
    "1 hour",
    "1 day",
    "1 week"
)

fun getMonthsOfYear() = arrayOf(
    "January", "Feburary", "March", "April",
    "May", "June", "July", "August", "September", "October", "November", "December"
)

fun getMonthId(mMonthName: String): String? {
    val mHashMap = hashMapOf(
        "January" to "1",
        "Feburary" to "2",
        "March" to "3",
        "April" to "4",
        "May" to "5",
        "June" to "6",
        "July" to "7",
        "August" to "8",
        "September" to "9",
        "October" to "10",
        "November" to "11",
        "December" to "12"
    )

    return mHashMap.get(mMonthName)
}

fun getLanguageArray() = arrayOf("English", "عربى")

fun getSortingSearchLabels() = listOf("Name", "Distance", "Rating", "Size", "Number of workers")

// 1=Name, 2=distance, 3=Rating, 4=Size, 5= number of workers
fun getSortingSearchId(mKey: String = "Name") = hashMapOf(
    "Name" to "1",
    "Distance" to "2",
    "Rating" to "3",
    "Size" to "4",
    "Number of workers" to "5"
).get(mKey)

fun getHtmlText(mData: String?): String {

    return if (mData != null) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            Html.fromHtml(mData).toString()
        } else {
            Html.fromHtml(mData, Html.FROM_HTML_MODE_LEGACY).toString()
        }
    } else
        ""


}

fun TextView.strikeThroughTextView() {
    this.paintFlags = this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Activity.showKeyboard() {
    showSoftKeyboard(currentFocus ?: View(this))
}

fun Fragment.showKeyboard() {
    view?.let { activity?.showSoftKeyboard(it) }
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

/*fun Fragment.warningBox(mMsg: String) {
    LovelyInfoDialog(activity)
        .setTopColorRes(R.color.colorPrimary)
        .setIcon(R.drawable.ic_app_icon_customer)
        // This will add Don't show again checkbox to the dialog. You can pass any ID as argument
        // .setNotShowAgainOptionEnabled(0)
        // .setNotShowAgainOptionChecked(true)
        .setTitle("Warning")
        .setMessage(mMsg)
        .show()
}

fun Activity.warningBox(mMsg: String) {
    LovelyInfoDialog(this)
        .setTopColorRes(R.color.colorPrimary)
        .setIcon(R.drawable.ic_app_icon_customer)
        //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
        //.setNotShowAgainOptionEnabled(0)
        //.setNotShowAgainOptionChecked(true)
        .setTitle("Warning")
        .setMessage(mMsg)
        .show()

}

fun Fragment.successBoxWithBack(mMsg: String) {

    LovelyStandardDialog(activity, LovelyStandardDialog.ButtonLayout.HORIZONTAL)

        .setButtonsColorRes(R.color.colorPrimary)
        .setIcon(R.drawable.ic_app_icon_customer)
        .setTitle("Success!")
        .setMessage(mMsg)
        .setPositiveButton(R.string.ok) {
            // Check for the network
            activity?.finish()
        }

        .show()
}

fun Activity.successBoxWithBack(mMsg: String) {
    LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.HORIZONTAL)

        .setButtonsColorRes(R.color.colorPrimary)
        .setIcon(R.drawable.ic_app_icon_customer)
        .setTitle("Success!")
        .setMessage(mMsg)
        .setPositiveButton(R.string.ok) {
            // Check for the network
            this.finish()
        }

        .show()
}*/

fun EditText.setCursorOnLast(length: Int) {
    Selection.setSelection(this.text, length)
}

fun Activity.statusBarTransparent() {
    /*   this.window.apply {
           clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
           addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
           decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
               statusBarColor = Color.TRANSPARENT
           }


           val rectangle = Rect()
           val window: Window = window
           window.getDecorView().getWindowVisibleDisplayFrame(rectangle)
           val statusBarHeight: Int = rectangle.top

       }*/
}

fun Fragment.showAlertDialog(context: Context) {
    val customBuilder =
        AlertDialog.Builder(context)
    customBuilder.setTitle("Twiclo")
    customBuilder.setMessage("Your session has been expired. Another device has logged into this account.")
    customBuilder.setNegativeButton(
        "OK"
    ) { dialog, which -> // MyActivity.this.finish();
       com.fidoo.user.data.session.SessionTwiclo(context).clearSession()
        startActivity(Intent(activity, AuthActivity::class.java))
        ActivityCompat.finishAffinity(activity!!)
    }
    // customBuilder.setIcon(R.drawable.logo)
    customBuilder.setCancelable(false)
    val dialog = customBuilder.create()
    dialog.show()

    /*  Button b = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

        if (b != null) {
            b.setBackgroundColor(getResources().getColor(R.color.color_white));
        }*/dialog.show()
}

fun Activity.showAlertDialog(context: Context) {
    val customBuilder =
        AlertDialog.Builder(context)
    customBuilder.setTitle("Twiclo")
    customBuilder.setMessage("Your session has been expired. Another device has logged into this account.")
    customBuilder.setNegativeButton(
        "OK"
    ) { _, _ -> // MyActivity.this.finish();
       com.fidoo.user.data.session.SessionTwiclo(context).clearSession()
        startActivity(Intent(this, SplashActivity::class.java))
        ActivityCompat.finishAffinity(this)
    }
    //customBuilder.setIcon(R.drawable.logo)
    customBuilder.setCancelable(false)
    val dialog = customBuilder.create()
    dialog.show()

    /*  Button b = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

        if (b != null) {
            b.setBackgroundColor(getResources().getColor(R.color.color_white));
        }*/dialog.show()
}

// dialog messages with the types
fun changeDateFormat(incomingDate: String?): String? { // parse the String "29/07/2013" to a java.util.Date object
    var date: Date? = null
    try {
        date = SimpleDateFormat("yyyy-mm-dd").parse(incomingDate)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    // format the java.util.Date object to the desired format
    return SimpleDateFormat("MM/dd/yy").format(date)
}

/*inline fun <reified T : Activity> Activity.navigate(params: List<IntentParams> = emptyList()) {
    val intent = Intent(this, T::class.java)
    for (parameter in params) {
        intent.putExtra(parameter.key, parameter.value)
    }
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(intent)

}*/

inline fun <reified T : Activity> Activity.navigateWithResultSet() {
    val intent = Intent(this, T::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivityForResult(intent, 202)

}

/*inline fun <reified T : Activity> Fragment.navigate(params: List<IntentParams> = emptyList()) {
    val intent = Intent(activity, T::class.java)
    for (parameter in params) {
        intent.putExtra(parameter.key, parameter.value)
    }
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(intent)
    //activity!!.finish()
}*/
/*

fun Activity.session(): SessionFoodTiger = SessionFoodTiger(this)
fun Activity.sessionNotNull(): SessionFoodTigerNotNull = SessionFoodTigerNotNull(this)

fun Fragment.session() = SessionFoodTiger(activity!!)
fun Fragment.sessionNotNull() = SessionFoodTigerNotNull(activity!!)

*/

fun appToolBar(
    activity: Context,
    toolbar: Toolbar?,
    isSetDisplayHomeAsUpEnabled: Boolean = true,
    isSetDisplayShowHomeEnabled: Boolean = true,
    isSetDisplayShowTitleEnabled: Boolean = false
) {

    (activity as AppCompatActivity).setSupportActionBar(toolbar!!)

    activity.supportActionBar?.apply {
        setDisplayHomeAsUpEnabled(isSetDisplayHomeAsUpEnabled)
        setDisplayShowHomeEnabled(isSetDisplayShowHomeEnabled)
        setDisplayShowTitleEnabled(isSetDisplayShowTitleEnabled)

    }
}

fun Activity.isNetworkConnected() =com.fidoo.user.data.CheckConnectivity(
    this
).isNetworkAvailable!!


fun Fragment.centerToast(toast_string: String) {
    val toast = Toast.makeText(activity, toast_string, Toast.LENGTH_SHORT)
    toast.setGravity(Gravity.CENTER, 0, 0)
    val view: View = toast.view!!
    view.setBackgroundColor(resources.getColor(R.color.colorAccent))
    val text = view.findViewById(android.R.id.message) as TextView
    /* Here you can do anything with above textview like text.setTextColor(Color.parseColor("#000000"));*/
    /*Here you can do anything with above textview like text.setTextColor(Color.parseColor("#000000"));*/
    toast.show()
}

fun Activity.centerToast(toast_string: String) {
    val toast: Toast = Toast.makeText(this, toast_string, Toast.LENGTH_SHORT)
    toast.setGravity(Gravity.CENTER, 0, 0)
    val view: View = toast.view!!
    view.setBackgroundColor(resources.getColor(R.color.colorAccent))
    val text = view.findViewById(android.R.id.message) as TextView
    /* Here you can do anything with above textview like text.setTextColor(Color.parseColor("#000000"));*/
    /*Here you can do anything with above textview like text.setTextColor(Color.parseColor("#000000"));*/
    toast.show()
}

fun Fragment.getHtmlText(mData: String?): String? {
    return if (mData != null) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            Html.fromHtml(mData).toString()
        } else {
            Html.fromHtml(mData, Html.FROM_HTML_MODE_LEGACY).toString()
        }
    } else ""
}

fun Activity.getHtmlText(mData: String?): String? {
    return if (mData != null) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            Html.fromHtml(mData).toString()
        } else {
            Html.fromHtml(mData, Html.FROM_HTML_MODE_LEGACY).toString()
        }
    } else ""
}

fun TextView.getHtmlText(mData: String?) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.setText(
            Html.fromHtml(mData, Html.FROM_HTML_MODE_LEGACY),
            TextView.BufferType.SPANNABLE
        )
    } else {
        this.setText(Html.fromHtml(mData), TextView.BufferType.SPANNABLE)
    }
}
/*

fun String.showShortMessage() =
    Toast.makeText(MyApplication.applicationContext(), this, Toast.LENGTH_SHORT).show()

fun String.showLongMessage() =
    Toast.makeText(MyApplication.applicationContext(), this, Toast.LENGTH_LONG).show()

*/

/*fun ImageView.loadImageFromServer(
    baseUrl: String? = "", url: String? = "",
    placeHolder: Int = R.drawable.ic_user_single
) {


    Glide.with(context).asBitmap().load(baseUrl + url).error(placeHolder)
        .placeholder(placeHolder).into(this)
}*/
/*

fun ImageView.loadImageFromServerWithGrayBg(baseUrl: String? = "", url: String? = "") {


    Glide.with(context).asBitmap().load(baseUrl + url).error(R.color.color_bg_gray)
        .placeholder(R.color.color_bg_gray).into(this)
}

fun ImageView.loadImageFromServer(baseUrl: String? = "", url: String? = "") {


    Glide.with(context).asBitmap().load(baseUrl + url).error(R.drawable.ic_user_single)
        .placeholder(R.drawable.ic_user_single).into(this)
}

fun CircleImageView.loadImageFromServer(baseUrl: String? = "", url: String? = "") {

    val requestOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)

    Glide.with(context).load(baseUrl + url)
        .error(R.drawable.ic_user_round)
        .placeholder(R.drawable.ic_user_round).apply(requestOptions).into(this)
}
*/

fun Activity.openWebLink(webLink: String?) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(webLink)
    // Always use string resources for UI text. This says something like "Share this photo with"
    val title = "Choose one"
    // Create and start the chooser
    val chooser = Intent.createChooser(intent, title)
    startActivity(chooser)
}

fun Fragment.openWebLink(webLink: String?) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(webLink)
    // Always use string resources for UI text. This says something like "Share this photo with"
    val title = "Choose one"
    // Create and start the chooser
    val chooser = Intent.createChooser(intent, title)
    startActivity(chooser)
}

fun Activity.shareMessage(shareBody: String = "", mPhone: String = "") {
    if (shareBody.trim().isNotEmpty()) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Trade App")
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        startActivity(Intent.createChooser(sharingIntent, "Share Using"))
    }

    if (mPhone.trim().isNotEmpty()) {
        val smsIntent =
            Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$mPhone"))
        //    smsIntent.setType("text");
        smsIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        //      smsIntent.setPackage("com.whatsapp");
        startActivity(Intent.createChooser(smsIntent, "Share Using"))
    }
}

fun Fragment.shareMessage(shareBody: String = "", mPhone: String = "") {
    if (shareBody.trim().isNotEmpty()) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Trade App")
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        startActivity(Intent.createChooser(sharingIntent, "Share Using"))
    }

    if (mPhone.trim().isNotEmpty()) {
        val smsIntent =
            Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$mPhone"))
        //    smsIntent.setType("text");
        smsIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        //      smsIntent.setPackage("com.whatsapp");
        startActivity(Intent.createChooser(smsIntent, "Share Using"))
    }
}


fun getFormattedDateSameAsWhatsApp(smsTimeInMilis: Long): String {
    val smsTime = Calendar.getInstance()
    smsTime.timeInMillis = smsTimeInMilis

    val now = Calendar.getInstance()

    val timeFormatString = "hh:mm aa"
    val dateTimeFormatString = "dd/MM/yy"
    //  val dateTimeFormatString = "EEE, MMM d, h:mm aa"
    val HOURS = 60 * 60 * 60

    return when {
        now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) -> {
            "Today at ${DateFormat.format(timeFormatString, smsTime)}"
        }
        now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1 -> {
            "Yesterday at ${DateFormat.format(timeFormatString, smsTime)}"
        }

        now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR) -> {
            DateFormat.format(dateTimeFormatString, smsTime).toString()
        }

        else -> {
            DateFormat.format("MMMM dd yyyy, hh:mm aa", smsTime).toString()
        }
    }
}

fun getFormattedDate(smsTimeInMilis: Long): String {
    val smsTime = Calendar.getInstance()
    smsTime.timeInMillis = smsTimeInMilis

    val now = Calendar.getInstance()

    val timeFormatString = "hh:mm aa"
    // val dateTimeFormatString = "EEE, MMM d, h:mm aa"
    val dateTimeFormatString = "MMM dd, hh:mm aa"
    val HOURS = 60 * 60 * 60

    return when {
        now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) -> {
            "Today " + DateFormat.format(timeFormatString, smsTime)
            // "${DateFormat.format(timeFormatString, smsTime)}"
        }
        now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1 -> {
            "Yesterday " + DateFormat.format(timeFormatString, smsTime)
        }

        now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR) -> {
            DateFormat.format(dateTimeFormatString, smsTime).toString()
        }

        else -> {
            DateFormat.format("MMM dd yyyy, hh:mm aa", smsTime).toString()
        }
    }
}

fun getTimeFormatForfromMillisecond(smsTimeInMilis: Long): String {
    val smsTime = Calendar.getInstance()
    smsTime.timeInMillis = smsTimeInMilis

    val now = Calendar.getInstance()

    val timeFormatString = "hh:mm aa"
    val dateTimeFormatString = "EEE, MMM dd, hh:mm aa"

    return when {
        now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) -> {
            "last seen today at ${DateFormat.format(timeFormatString, smsTime)}"
        }
        now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1 -> {
            "last seen yesterday at  ${DateFormat.format(timeFormatString, smsTime)}"
        }

        now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR) -> {
            "last seen ${DateFormat.format(dateTimeFormatString, smsTime)}"
        }

        else -> {
            DateFormat.format("MMM dd yy, hh:mm aa", smsTime).toString()
        }
    }
}

fun openPlaystore(mUrl: String) {
//    val appPackageName = MyApplication.activityContext() ?.packageName
//    try {
//        MyApplication.activityContext()?.startActivity(
//            Intent(
//                Intent.ACTION_VIEW,
//                Uri.parse("market://details?id=${appPackageName}")
//            )
//        )
//    } catch (anfe: ActivityNotFoundException) {
//        MyApplication.activityContext()?.startActivity(
//            Intent(
//                Intent.ACTION_VIEW,
//                Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
//            )
//        )
//    }

    val intent = Intent()
    intent.action = Intent.ACTION_VIEW
    intent.data = Uri.parse(mUrl)
    //   MyApplication.activityContext()?.startActivity(intent)
}

fun viewFile(path: String) {

    val file = File(path) // path = your file path

//    val mimeType: String? = if (file.name.endsWith("pdf")) {
//        "application/pdf"
//    } else {
//        MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(path))
//    }

    val mimeType: String? = MimeTypeMap.getSingleton()
        .getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(path))

    mimeType?.let {
        val uripath = Uri.parse(path)

        val intent: Intent? = Intent(Intent.ACTION_VIEW).apply {
            putExtra("path", path)
            putExtra("mimeType", mimeType)
            type = mimeType
            setDataAndType(uripath, mimeType)
        }

        try {
            /* MyApplication.activityContext()
                 ?.startActivity(Intent.createChooser(intent, "Open File"))
         */
        } catch (e: ActivityNotFoundException) {
            // Instruct the user to install a PDF reader here, or something
            e.printStackTrace()
        }
    }

    // startActivity(intent)

}

fun Activity.pickImageFromGallary() {
    permissionsReadWriteWithCamera()
    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    intent.type = "image/*"
    intent.action = Intent.ACTION_GET_CONTENT
    startActivityForResult(
        Intent.createChooser(intent, "Select Image"),
        TRADEAPP_PICK_IMAGE_FROM_GALLARY
    )
}

fun Activity.pickVideoFromGallary() {
    permissionsReadWriteWithCamera()
    val intent = Intent(Intent.ACTION_PICK) //MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
    intent.type = "video/*"
    intent.action = Intent.ACTION_GET_CONTENT
    startActivityForResult(
        Intent.createChooser(intent, "Select Video"),
        TRADEAPP_PICK_VIDEO_FROM_GALLARY
    )
}

fun Activity.pickAudioFromGallary() {
    permissionsReadWrite()
    val intent = Intent(Intent.ACTION_PICK)  //, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
    intent.type = "audio/*"
    intent.action = Intent.ACTION_GET_CONTENT
    startActivityForResult(
        Intent.createChooser(intent, "Select Audio"),
        TRADEAPP_PICK_AUDIO_FROM_GALLARY
    )
}

fun Activity.pickFileFromGallary() {
    permissionsReadWrite()
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
    intent.type = "*/*"
    //intent.action = Intent.ACTION_GET_CONTENT
    startActivityForResult(
        Intent.createChooser(intent, "Select File"),
        TRADEAPP_PICK_FILE__FROM_GALLARY
    )
}

fun Activity.permissionsReadWrite() {
    if (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            1
        )
        return
    }
}

fun Fragment.permissionsReadWrite() {
    if (ContextCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            activity!!,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            1
        )
        return
    }
}

fun Activity.permissionsReadWriteWithCamera() {
    if (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) != PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_PHONE_STATE
        ) != PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS
            ), 1
        )

        return
    }
}

fun Activity.permissionsForVoiceCall() {
    if (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) != PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_PHONE_STATE
        ) != PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
        ) != PackageManager.PERMISSION_GRANTED


    ) {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS
            ),

            1
        )

        return
    }
}

fun Activity.showSoftKeyboard(view: View) {
    if (view.requestFocus()) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun Fragment.showSoftKeyboard(view: View) {
    if (view.requestFocus()) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}


fun Activity.getRootDirPath(): String? {
    return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
        val file = ContextCompat.getExternalFilesDirs(
            applicationContext,
            null
        )[0]
        file.absolutePath
    } else {
        applicationContext.filesDir.absolutePath
    }
}

fun Fragment.getRootDirPath() =
    (if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
        val file = ContextCompat.getExternalFilesDirs(activity!!.applicationContext, null)[0]
        file.absolutePath
    } else {
        activity!!.applicationContext.filesDir.absolutePath
    })


fun getProgressDisplayLine(currentBytes: Long, totalBytes: Long): String {
    return getBytesToMBString(currentBytes) + "/" + getBytesToMBString(totalBytes)
}

fun getBytesToMBString(bytes: Long) =
    String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00))

fun Fragment.sendEmailWithAttachment(mFile: Uri) {

    startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
        // The intent does not have a URI, so declare the "text/plain" MIME type
        //  type =                   //  HTTP.PLAIN_TEXT_TYPE
        type = "vnd.android.cursor.dir/email"
        // putExtra(Intent.EXTRA_EMAIL, arrayOf("jon@example.com")) // recipients
        putExtra(Intent.EXTRA_SUBJECT, "Chat")
        putExtra(Intent.EXTRA_TEXT, "Please find chat file attached with this email.")
        putExtra(Intent.EXTRA_STREAM, mFile)
        // You can also attach multiple items by passing an ArrayList of Uris


    }, "Send email..."))
}


fun Fragment.createDirAndGetPath(): File? {

    //if there is no SD card, create new directory objects to make directory on device
    if (Environment.getExternalStorageState() == null) { //create new file directory object
        var directory = File(Environment.getDataDirectory().toString() + "/TradeApp/ChatFiles/")


        /*
         * this checks to see if there are any previous test photo files
         * if there are any photos, they are deleted for the sake of
         * memory
         */
//            if (photoDirectory.exists()) {
//                val dirFiles: Array<File> = photoDirectory.listFiles()
//                if (dirFiles.size != 0) {
//                    for (ii in 0..dirFiles.size) {
//                        dirFiles[ii].delete()
//                    }
//                }
//            }

        // if no directory exists, create new directory
        if (!directory.exists()) {
            directory.mkdir()
        }

        return directory

        // if phone does have sd card
    } else if (Environment.getExternalStorageState() != null) { // search for directory on SD card
        var directory =
            File(Environment.getExternalStorageDirectory().toString() + "/TradeApp/ChatFiles/")
//            photoDirectory = File(Environment.getExternalStorageDirectory()
//                    .toString() + "/Robotium-Screenshots/")
//            if (photoDirectory.exists()) {
//                var dirFiles: Array<File>? = photoDirectory.listFiles()
//                if (dirFiles!!.size > 0) {
//                    for (ii in dirFiles.indices) {
//                        dirFiles[ii].delete()
//                    }
//                    dirFiles = null
//                }
//            }
        // if no directory exists, create new directory to store test

        // results
        if (!directory.exists()) {
            directory.mkdir()
        }

        return directory
    } // end of SD card checki
    else {
        return null
    }

}


fun Fragment.opneAddContactDilaog() {
    val i = Intent(Intent.ACTION_INSERT)
    i.type = ContactsContract.Contacts.CONTENT_TYPE
    i.putExtra("finishActivityOnSaveCompleted", true) // Fix for 4.0.3 +
    startActivityForResult(i, PICK_CONTACT_REQUEST)
}

fun Activity.opneAddContactDilaog() {
    val i = Intent(Intent.ACTION_INSERT)
    i.type = ContactsContract.Contacts.CONTENT_TYPE
    i.putExtra("finishActivityOnSaveCompleted", true) // Fix for 4.0.3 +
    startActivityForResult(i, PICK_CONTACT_REQUEST)
}

// this method works when the local pdf is selected from teh phone
//infix fun ImageView.retreiveThumnailFromPdf(pdfUri: String): Bitmap? {
//    var bitmap: Bitmap?
//    //   var   fd:
//    var pageNumber = 0
//    val pdfiumCore = PdfiumCore(this.context)
//
//    try {
//        val fd = this.context.contentResolver.openFileDescriptor(pdfUri.toUri(), "r")
//        // val fd = this.context.contentResolver.openFileDescriptor(Uri.fromFile(File(pdfUri)), "r")
//        val pdfDocument = pdfiumCore.newDocument(fd)
//        pdfiumCore.openPage(pdfDocument, pageNumber)
//
//        val width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber)
//        val height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber)
//
//        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
//        pdfiumCore.renderPageBitmap(pdfDocument, bitmap, pageNumber, 0, 0, width, height)
//
//        pdfiumCore.let {
//            it.closeDocument(pdfDocument) // important!
//        }
//    } catch (e: Exception) {
//        e.printStackTrace()
//        throw Throwable("Exception in retreive Pdf image (String pdfPath) ${e.message}")
//    }
//    return bitmap
//}


infix fun String.retriveVideoFrameFromVideoUrl(videoPath: String): Bitmap? {
    var bitmap: Bitmap?

    var mediaMetadataRetriever: MediaMetadataRetriever? = null
    try {
        mediaMetadataRetriever = MediaMetadataRetriever().apply {
            setDataSource(videoPath, HashMap<String, String>())
        }
        bitmap =
            mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST).apply {
                this?.let {
                    Bitmap.createScaledBitmap(it, 50, 50, false)
                }
            }

    } catch (e: Exception) {
        e.printStackTrace()
        throw Throwable("Exception in retrive VideoFrameFromVideo(String videoPath) ${e.message}")

    } finally {
        mediaMetadataRetriever?.let {
            it.release()
        }

    }
    return bitmap
}

infix fun String.retreiveVideoLength(videoPath: String): String? {
    var mLen: String? = null

    var mediaMetadataRetriever: MediaMetadataRetriever? = null
    try {
        val mimeType: String? = MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(videoPath))
        mimeType?.let { mimeTp ->

            mediaMetadataRetriever = MediaMetadataRetriever().apply {
                setDataSource(
                    videoPath,
                    HashMap<String, String>()
                )
            }

            val mLength: String? =
                mediaMetadataRetriever?.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            mLength?.let {
                mLen = String.format(
                    "%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(mLength.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(mLength.toLong()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mLength.toLong()))
                )
            }
        }


    } catch (e: Exception) {
        e.printStackTrace()
        throw Throwable("Exception in retrive VideoFrameFromVideo(String videoPath) ${e.message}")

    } finally {
        mediaMetadataRetriever?.let {
            it.release()
        }

    }
    return mLen
}

/*
fun getLocationFromAddress(mAddressText: String?): String {
    val coder = Geocoder(MyApplication.applicationContext())
    val address: List<Address>?
    try {
        address = coder.getFromLocationName(mAddressText, 1)


        if (address != null && address.isNotEmpty()) {
            val location = address[0]
            // String state1 = location.getAdminArea();
            //  val cntry = location.countryName
            // set the city if not set in teh field
            val mLat = location.latitude.toString()
            // set the zip code if not set in the field
            val mLong = location.longitude.toString()

            return "$mLat:$mLong"
        } else {
            return "0:0"
        }

    } catch (ioExc: IOException) {
        Log.wtf("", ioExc.message)
        return "0:0"
    }
}*/
































