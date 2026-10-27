package com.kachat.app.services

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * Receives FCM messages and turns them into notifications via [NotificationHelper].
 *
 * KaChat Wallet registers only for "Received KAS" on its own addresses and .kachat name events
 * (PUSH_SERVICE.md), so two types are handled:
 *   address_activity : type, address, tx_id, [amount_sompi | amount_kas], title
 *   name_event       : type, event, name, [amount], [days], [tx_id], title, body
 * Anything else - a chat push the service sent before it learned this device is a wallet - is
 * claimed and dropped, never shown. The chat handling went with the chats (iOS wallet 301ebfb).
 */
@AndroidEntryPoint
class KaChatFirebaseMessagingService : FirebaseMessagingService() {

    @Inject lateinit var notificationHelper: NotificationHelper
    @Inject lateinit var pushRegistrationManager: PushRegistrationManager

    override fun onNewToken(token: String) {
        // FCM rotated the token — re-register it with the indexer (signed with the wallet key).
        Log.i(TAG, "FCM token rotated, re-registering with the push service")
        pushRegistrationManager.onTokenRefreshed(token)
    }

    /**
     * The push service's "Received" push for one of the wallet's own addresses (`address_activity`,
     * thread `funds:<address>`). No banner while the app is open - its own Address Activity
     * notification already announces that receive (iOS wallet 1806a69) - and one banner per
     * transaction either way: whichever of the two claims the txid first shows it. A tap opens
     * that address's history, as the local notification does.
     */
    private suspend fun handleAddressActivity(data: Map<String, String>) {
        val address = data["address"]?.takeIf { it.isNotBlank() } ?: return
        val txId = data["tx_id"]?.takeIf { it.isNotBlank() } ?: return
        if (notificationHelper.isAppInForeground) return
        if (!notificationHelper.claimForBanner(txId)) return
        val sompi = data["amount_sompi"]?.toLongOrNull()
        val amount = sompi?.let { AddressActivityNotifier.formatKas(it) } ?: data["amount_kas"].orEmpty()
        notificationHelper.showAddressActivity(
            title = if (amount.isNotEmpty()) "Received $amount ${com.kachat.app.util.KaspaUnit.symbol}" else data["title"].orEmpty().ifEmpty { "Funds received" },
            text = AddressActivityNotifier.shortAddress(address),
            dedupeKey = txId,
            address = address,
        )
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val data = message.data
        if (data.isEmpty()) {
            Log.d(TAG, "push received with empty data payload, ignoring")
            return
        }

        val type = data["type"].orEmpty()
        Log.i(TAG, "push received: type=$type txId=${data["tx_id"].orEmpty().take(16)}")

        // NotificationHelper.show* are suspend and quick; block so the work completes before the
        // service is torn down.
        runBlocking {
            try {
                // Any push about the other network - the service this device left when it moved
                // between mainnet and testnet still pushing it - stays silent, whatever its type
                // (iOS d946a65 rule, audit AND-006).
                if (com.kachat.app.util.PushNetworkFilter.isOtherNetwork(data, com.kachat.app.util.KaspaNetwork.launch)) {
                    data["tx_id"]?.takeIf { it.isNotBlank() }?.let { notificationHelper.claimWithoutNotifying(it) }
                    Log.i(TAG, "Silenced a $type push from the other network")
                    return@runBlocking
                }
                when (type) {
                    // .kachat name events (KACHAT_NAMES_INDEXER.md Part E): offers, sales,
                    // renewal reminders, written in the device's language (iOS beeedd4).
                    "name_event" -> handleNameEvent(data)
                    "address_activity" -> handleAddressActivity(data)
                    else -> {
                        data["tx_id"]?.takeIf { it.isNotBlank() }?.let { notificationHelper.claimWithoutNotifying(it) }
                        Log.i(TAG, "Ignoring a $type push: KaChat Wallet has no chats")
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to handle push (type=$type): ${e.message}")
            }
        }
    }

    /**
     * A `.kachat` name event, written in the device's language (iOS NotificationService
     * handleNameEvent). Payload (indexer Part E): `event` (name_offer, name_sold,
     * name_offer_accepted, name_renewal_open, name_expiring, name_grace; name_lapsed,
     * name_offer_declined and name_offer_refunded for when the indexer sends them, iOS 86471dd),
     * `name` (without
     * .kachat), optional `amount` (sompi) and `days`. An event this build doesn't know keeps the
     * server's own text. Tapping it opens the name.
     */
    private suspend fun handleNameEvent(data: Map<String, String>) {
        val rawName = data["name"]?.trim()?.lowercase()?.takeIf { it.isNotEmpty() } ?: return
        val bare = rawName.removeSuffix(".kachat")
        val name = "$bare.kachat"
        val amount = data["amount"]?.trim()?.toLongOrNull()?.let { sompi ->
            kasText(sompi) + " " + com.kachat.app.util.KaspaUnit.symbol
        }
        val days = data["days"]?.trim()?.toIntOrNull()
        val (title, body) = when (data["event"]) {
            "name_offer" -> getString(com.kachat.app.R.string.kn_push_new_offer, name) to
                (amount?.let { getString(com.kachat.app.R.string.kn_push_offered, it) } ?: getString(com.kachat.app.R.string.kn_push_offer_no_amount))
            "name_sold" -> getString(com.kachat.app.R.string.kn_push_sold, name) to
                (amount?.let { getString(com.kachat.app.R.string.kn_push_paid, it) } ?: getString(com.kachat.app.R.string.kn_push_listing_bought))
            "name_offer_accepted" -> getString(com.kachat.app.R.string.kn_push_offer_accepted) to getString(com.kachat.app.R.string.kn_push_yours_now, name)
            "name_renewal_open" -> getString(com.kachat.app.R.string.kn_push_renew, name) to getString(com.kachat.app.R.string.kn_push_renewal_open)
            // days on mainnet's yearly clock; testnet's periods are minutes long (days = 0)
            "name_expiring" -> getString(com.kachat.app.R.string.kn_push_expires_soon, name) to when {
                days != null && days > 1 -> getString(com.kachat.app.R.string.kn_push_days_left, days)
                days == 1 -> getString(com.kachat.app.R.string.kn_push_one_day_left)
                else -> getString(com.kachat.app.R.string.kn_push_renew_soon)
            }
            "name_grace" -> getString(com.kachat.app.R.string.kn_push_expired, name) to getString(com.kachat.app.R.string.kn_push_grace_soon)
            "name_lapsed" -> getString(com.kachat.app.R.string.kn_bell_no_longer_yours_title, name) to getString(com.kachat.app.R.string.kn_bell_lapsed_available_body)
            "name_offer_declined" -> getString(com.kachat.app.R.string.kn_bell_declined_title, name) to
                com.kachat.app.util.KaspaUnit.label(getString(com.kachat.app.R.string.kn_bell_declined_body))
            "name_offer_refunded" -> getString(com.kachat.app.R.string.kn_bell_offer_expired_title, name) to
                com.kachat.app.util.KaspaUnit.label(getString(com.kachat.app.R.string.kn_bell_offer_expired_body))
            else -> data["title"].orEmpty().ifEmpty { name } to data["body"].orEmpty()
        }
        val dedupeKey = data["tx_id"]?.takeIf { it.isNotBlank() } ?: "${data["event"]}:$bare:${data["amount"]}:${data["days"]}"
        notificationHelper.showKachatName(title = title, text = body, name = bare, dedupeKey = dedupeKey)
    }

    /** Sompi as KAS text: up to 8 decimals, trailing zeros dropped (iOS kasText). */
    private fun kasText(sompi: Long): String =
        String.format(java.util.Locale.US, "%.8f", sompi / 100_000_000.0).trimEnd('0').trimEnd('.')

    companion object {
        // Same tag as PushRegistrationManager: `adb logcat -s KaChatPush` shows registrations,
        // token rotations, and every received push in one stream.
        private const val TAG = PushRegistrationManager.TAG
    }
}
