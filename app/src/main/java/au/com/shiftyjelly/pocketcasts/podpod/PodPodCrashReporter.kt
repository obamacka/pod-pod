package au.com.shiftyjelly.pocketcasts.podpod

import android.app.Activity
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.MediaStore
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.system.exitProcess

/**
 * Pod-Pod: crash reporter. Installed by [PodPodCrashInstaller] (a ContentProvider, so it runs before
 * Application.onCreate). On an uncaught exception it saves the stack trace to app storage and to
 * Downloads/podpod-crash-<time>.txt, then shows [PodPodCrashActivity] in a separate process with a Share button.
 */
internal object PodPodCrashReporter {
    private const val CRASH_PROCESS_SUFFIX = ":podpod_crash"
    const val EXTRA_REPORT = "podpod_crash_report"

    fun install(context: Context) {
        val appContext = context.applicationContext ?: context
        if (currentProcessName().endsWith(CRASH_PROCESS_SUFFIX)) return
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val report = buildReport(appContext, thread, throwable)
                saveReport(appContext, report)
                val intent = Intent(appContext, PodPodCrashActivity::class.java)
                    .putExtra(EXTRA_REPORT, report)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                appContext.startActivity(intent)
                Process.killProcess(Process.myPid())
                exitProcess(10)
            } catch (_: Throwable) {
                previous?.uncaughtException(thread, throwable)
            }
        }
    }

    private fun currentProcessName(): String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        android.app.Application.getProcessName()
    } else {
        runCatching { File("/proc/self/cmdline").readText().trim(Char(0)) }.getOrDefault("")
    }

    private fun buildReport(context: Context, thread: Thread, throwable: Throwable): String {
        val stack = StringWriter().also { throwable.printStackTrace(PrintWriter(it)) }.toString()
        @Suppress("DEPRECATION")
        val version = runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull()
        return buildString {
            appendLine("Pod-Pod crash report")
            appendLine("Time: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US).format(Date())}")
            appendLine("App: ${context.packageName} $version")
            appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}, Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            appendLine("Thread: ${thread.name}")
            appendLine()
            append(stack)
        }
    }

    private fun saveReport(context: Context, report: String) {
        runCatching { File(context.filesDir, "podpod-last-crash.txt").writeText(report) }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            runCatching {
                val name = "podpod-crash-${SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())}.txt"
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                }
                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                if (uri != null) {
                    context.contentResolver.openOutputStream(uri)?.use { it.write(report.toByteArray()) }
                }
            }
        }
    }
}

/** Pod-Pod: installs [PodPodCrashReporter] before the Application is created. */
class PodPodCrashInstaller : ContentProvider() {
    override fun onCreate(): Boolean {
        context?.let { PodPodCrashReporter.install(it) }
        return true
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? = null

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0
}

/** Pod-Pod: shows the last crash with Share and Close buttons. Uses only platform views so it cannot crash on app themes. */
class PodPodCrashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val report = intent.getStringExtra(PodPodCrashReporter.EXTRA_REPORT)
            ?: runCatching { File(filesDir, "podpod-last-crash.txt").readText() }.getOrDefault("No crash report found.")
        val padding = (16 * resources.displayMetrics.density).toInt()
        val title = TextView(this).apply {
            text = "Pod-Pod crashed. Share this report with HAL."
            textSize = 18f
            setPadding(padding, padding, padding, padding / 2)
        }
        val share = Button(this).apply {
            text = "Share report"
            setOnClickListener {
                val send = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, report)
                startActivity(Intent.createChooser(send, "Share crash report"))
            }
        }
        val close = Button(this).apply {
            text = "Close"
            setOnClickListener { finishAndRemoveTask() }
        }
        val buttons = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(padding, 0, padding, 0)
            addView(share)
            addView(close)
        }
        val body = TextView(this).apply {
            text = report
            textSize = 11f
            setTextIsSelectable(true)
            setPadding(padding, padding / 2, padding, padding)
        }
        val scroll = ScrollView(this).apply { addView(body) }
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            fitsSystemWindows = true
            addView(title)
            addView(buttons)
            addView(scroll, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        }
        setContentView(root)
    }
}
