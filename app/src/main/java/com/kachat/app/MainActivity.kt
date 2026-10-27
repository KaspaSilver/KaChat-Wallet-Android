package com.kachat.app

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.kachat.app.services.CrashRecorder
import com.kachat.app.services.NotificationHelper
import com.kachat.app.ui.theme.KaChatTheme
import com.kachat.app.ui.KaChatApp
import com.kachat.app.ui.screens.KaChatLink
import com.kachat.app.viewmodels.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

/**
 * Single Activity — all navigation is handled in Compose via NavHost.
 *
 * Extends [AppCompatActivity] (rather than plain `FragmentActivity`, which it's a superset of)
 * because the per-app language API (`AppCompatDelegate.setApplicationLocales`, used by the
 * Language setting in Settings) relies on `AppCompatActivity`'s `attachBaseContext` override to
 * apply the locale override on API levels below 33.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    // "cold" / "spending" — which wallet screen an address-activity receipt tap opens.
    private var pendingWalletActivityKind by mutableStateOf<String?>(null)
    // The one own address a "Received" receipt named: its History opens in a sheet (iOS 13046ad).
    // Held here until the shell is up and takes it, which is what covers a cold start (iOS's
    // OwnAddressRoute.pending).
    private var pendingOwnAddress by mutableStateOf<String?>(null)

    // Android 13+'s notification permission is asked from the tabs, once, not at launch: see
    // MainShell's NotificationPermissionOnce (iOS wallet 1806a69).

    /** A side step of opening the app: a failure is logged, never the end of it. */
    private fun guarded(what: String, block: () -> Unit) =
        com.kachat.app.util.StartupSafety.step("MainActivity", what, block)

    /** The launching (or new) Intent's notification target, links and share. */
    private fun handleLaunchIntent(intent: Intent?) {
        if (intent == null) return
        guarded("notification target") { consumeNotificationTarget(intent) }
        // The only link the wallet opens; profile, public-chat and KaPosts links and the share
        // target went with the chats (iOS wallet 301ebfb).
        guarded("payment request link") { handlePaymentRequestDeepLink(intent) }
        guarded("clear intent payload") { clearConsumedIntentPayload(intent) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Phones lock to portrait; tablets (sw>=600dp, see res/values-sw600dp/bools.xml) rotate
        // freely - mirroring iOS (iPhone portrait-only, iPad any orientation).
        requestedOrientation = if (resources.getBoolean(R.bool.lock_portrait)) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_FULL_USER
        }
        // Balances, chats and the seed screen used to sit in the task switcher's thumbnail of the
        // app in plain view. This is Android's own switch for exactly that, and unlike FLAG_SECURE
        // it does not also block screenshots and screen recording, which people legitimately use
        // on a chat - and it cannot fight the FLAG_SECURE the seed and private-key screens set and
        // clear for themselves. Below API 33 there is no equivalent that leaves screenshots alone,
        // so those devices keep the thumbnail. iOS covers the same exposure with a blur (7e39943).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            setRecentsScreenshotEnabled(false)
        }
        enableEdgeToEdge()
        // Everything up to setContent is a side step - a permission prompt, a notification or
        // link to route. Each is guarded on its own: one that throws is
        // logged, and the app still opens (KaChatApplication's startup rule).
        handleLaunchIntent(intent)
        setContent {
            val walletViewModel: WalletViewModel = hiltViewModel()
            // Settings > Customization > Appearance: System follows the device, as on iOS.
            val appearance by walletViewModel.appearance.collectAsState()
            KaChatTheme(darkTheme = appearance.isDark(androidx.compose.foundation.isSystemInDarkTheme())) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    KaChatApp(
                        pendingWalletActivityKind = pendingWalletActivityKind,
                        onPendingWalletActivityHandled = { pendingWalletActivityKind = null },
                        pendingOwnAddress = pendingOwnAddress,
                        onPendingOwnAddressHandled = { pendingOwnAddress = null }
                    )
                    CrashNotice()
                }
            }
        }
    }

    /**
     * Shown once on the launch after the app crashed: the previous process died before anyone
     * could see why, and its record is the one thing that turns "it will not open" into a fix.
     * Share sends the newest report to a share sheet (mail, Telegram, anything); Not now keeps
     * it for the diagnostics archive under Settings > Diagnostics.
     */
    @androidx.compose.runtime.Composable
    private fun CrashNotice() {
        val crashFile = remember { CrashRecorder.unseenCrash(this) }
        var show by remember { mutableStateOf(crashFile != null) }
        if (!show || crashFile == null) return
        com.kachat.app.ui.theme.IosAlertDialog(
            onDismissRequest = { show = false; CrashRecorder.markSeen(this) },
            title = { Text("KaPay closed unexpectedly") },
            text = { Text("The app did not close normally the last time it ran. A report of what happened was saved. Sharing it with the developer is the quickest way to get it fixed - it contains no messages, keys or addresses.") },
            confirmButton = {
                TextButton(onClick = {
                    show = false
                    CrashRecorder.markSeen(this)
                    shareCrashReport(crashFile)
                }) { Text("Share report") }
            },
            dismissButton = {
                TextButton(onClick = { show = false; CrashRecorder.markSeen(this) }) { Text("Not now") }
            }
        )
    }

    private fun shareCrashReport(file: File) {
        runCatching {
            val uri = androidx.core.content.FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
            val send = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "KaPay crash report")
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(send, "Share crash report"))
        }.onFailure { android.util.Log.w("MainActivity", "Could not share crash report", it) }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleLaunchIntent(intent)
    }

    /**
     * Strips the link/share payload once the handlers above have read it.
     *
     * The Activity keeps its launching Intent, so a configuration change re-runs `onCreate`
     * against it. Settings > Language does exactly that (`AppCompatDelegate.setApplicationLocales`
     * recreates the Activity), and without this a language change re-opened the last payment
     * request that had been opened from a link.
     * The notification extras are stripped in [consumeNotificationTarget] for the same reason;
     * a link carries its payload in the action/data instead of in our own extras.
     *
     * Safe to run immediately: every handler reads what it needs synchronously before any
     * suspending work.
     */
    private fun clearConsumedIntentPayload(intent: Intent) {
        if (intent.action == Intent.ACTION_VIEW ||
            intent.action == Intent.ACTION_SEND ||
            intent.action == Intent.ACTION_SEND_MULTIPLE
        ) {
            intent.action = null
            intent.data = null
            intent.type = null
            listOf(
                Intent.EXTRA_TEXT,
                Intent.EXTRA_STREAM,
                Intent.EXTRA_SHORTCUT_ID,
            ).forEach(intent::removeExtra)
        }
    }

    /**
     * Reads whichever notification target the launching Intent carries, then strips it.
     *
     * Two shapes arrive here:
     *  - The app's OWN notifications ([NotificationHelper]), which carry explicit extras.
     *  - A notification FCM drew itself. Any push carrying an FCM `notification` block is shown
     *    by the OS while the app is backgrounded or dead, and `onMessageReceived` never runs for
     *    it in that state: no [NotificationHelper] intent is ever built, and the tap instead
     *    launches this Activity with the push's DATA payload as plain string extras. Those keys
     *    are the server's (`type`, `channel`, `sender`, the post id), not ours, so without
     *    [applyFcmNotificationTarget] such a tap just restored the last screen the user was on.
     *    The two in-repo descriptions of the server's choice disagree (this app's
     *    `KaChatFirebaseMessagingService` header says broadcast/KaPosts ship a `notification`
     *    block; PUSH_ANDROID_SETUP.md says everything is data-only), so both shapes are handled
     *    here rather than betting on one.
     *
     * Stripping matters because a configuration change (Settings > Language recreates the
     * Activity) re-runs `onCreate` against the same Intent: without it, changing the language
     * jumped the user back into whatever conversation the last tapped notification was for.
     */
    private fun consumeNotificationTarget(intent: Intent) {
        pendingWalletActivityKind = intent.getStringExtra(NotificationHelper.EXTRA_WALLET_ACTIVITY_KIND)
        // One address got the Kaspa: open that address's History rather than the wallet tab -
        // the kind is then only the fallback (iOS 13046ad).
        pendingOwnAddress = intent.getStringExtra(NotificationHelper.EXTRA_WALLET_ACTIVITY_ADDRESS)?.takeIf { it.isNotEmpty() }
        if (pendingOwnAddress != null) pendingWalletActivityKind = null
        // A .kachat name notification: open that name (iOS beeedd4).
        val kachatName = intent.getStringExtra(NotificationHelper.EXTRA_KACHAT_NAME)?.takeIf { it.isNotBlank() }
        kachatName?.let {
            com.kachat.app.ui.screens.KachatDeepLink.pendingName.value =
                com.kachat.app.services.kachatnames.KachatNames.Codec.normalize(it.removeSuffix(".kachat"))
        }
        if (pendingWalletActivityKind == null && pendingOwnAddress == null && kachatName == null) {
            applyFcmNotificationTarget(intent)
        }
        listOf(
            NotificationHelper.EXTRA_WALLET_ACTIVITY_KIND,
            NotificationHelper.EXTRA_WALLET_ACTIVITY_ADDRESS,
            NotificationHelper.EXTRA_KACHAT_NAME,
            FCM_KEY_TYPE,
        ).forEach(intent::removeExtra)
    }

    /**
     * Routes a tap on a notification FCM drew from its own `notification` block, using the data
     * payload the OS handed us as Intent extras. The wallet's two push types are mapped; a chat
     * type (a push sent before the service learned this device is a wallet) opens nothing
     * (iOS wallet 301ebfb).
     */
    private fun applyFcmNotificationTarget(intent: Intent) {
        when (intent.getStringExtra(FCM_KEY_TYPE)) {
            "name_event" -> intent.getStringExtra("name")?.takeIf { it.isNotBlank() }?.let {
                com.kachat.app.ui.screens.KachatDeepLink.pendingName.value =
                    com.kachat.app.services.kachatnames.KachatNames.Codec.normalize(it.lowercase().removeSuffix(".kachat"))
            }
            // KaChat Wallet's "Received" push on one of its own addresses: that address's History,
            // as the app's own Address Activity notification opens (PUSH_SERVICE.md).
            "address_activity" -> pendingOwnAddress = intent.getStringExtra("address")?.takeIf { it.isNotBlank() }
        }
        // Stripped once read, so a configuration change cannot route the same tap twice.
        listOf("name", "address", "thread_id", "tx_id").forEach(intent::removeExtra)
    }

    /** The hosts a KaChat link can arrive on: the one the app writes, and the one it used to. */
    private fun isKaChatWebHost(host: String?): Boolean {
        val clean = host?.lowercase()?.removePrefix("www.") ?: return false
        return clean == KaChatLink.WEB_HOST || clean in KaChatLink.LEGACY_WEB_HOSTS
    }

    /** kachat://pay/<address>?... or https://kachat.app/pay/<address>?... - someone's payment
     *  request (iOS c6851d6). Everything in it is checked - the address on this network, the exact
     *  amount, the memo, a signature when it carries one - before Send Kaspa opens filled in (your
     *  own shows it instead); nothing is ever sent without slide to send. */
    private fun handlePaymentRequestDeepLink(intent: Intent) {
        if (intent.action != Intent.ACTION_VIEW) return
        val uri = intent.data ?: return
        val isPayLink = when {
            uri.scheme.equals("kachat", ignoreCase = true) -> uri.host.equals("pay", ignoreCase = true)
            uri.scheme.equals("https", ignoreCase = true) || uri.scheme.equals("http", ignoreCase = true) ->
                isKaChatWebHost(uri.host) && uri.pathSegments.firstOrNull() == "pay"
            else -> false
        }
        if (!isPayLink) return
        val ref = com.kachat.app.ui.screens.KaChatLink.parse(uri.toString()) as? com.kachat.app.ui.screens.KaChatLinkRef.PaymentRequest
            ?: return
        com.kachat.app.ui.screens.PaymentRequestRouter.open(ref.request)
    }

    companion object {
        /** FCM data-payload key that reaches us as an Intent extra when the OS drew the
         *  notification itself - see [applyFcmNotificationTarget]. */
        private const val FCM_KEY_TYPE = "type"
    }
}
