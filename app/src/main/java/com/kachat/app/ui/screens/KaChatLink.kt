package com.kachat.app.ui.screens

// ---------------------------------------------------------------------------------------------
// In-app KaChat links (shared with iOS - both platforms emit and accept exactly these forms).
// Moved out of the deleted LinkPreviewCard.kt; the wallet keeps only the profile and payment
// request links - KaPosts and broadcast-room links went with the chats (iOS wallet 301ebfb).
// ---------------------------------------------------------------------------------------------

/**
 * A KaChat link found in text. Tapping one routes inside the app instead of handing the URL to a
 * browser.
 *
 * Wire forms (identical on iOS):
 *   Profile        kachat://profile/<address>   https://kachat.app/u/<address>
 *   Payment req.   kachat://pay/<address>?...   https://kachat.app/pay/<address>?amount=<KAS>&memo=<text>[&chat=1]&by=<address>&sig=<hex>
 *                  (see [com.kachat.app.models.PaymentRequest], iOS c6851d6 / 7c71a98)
 *
 * A profile's `<address>` is a full Kaspa address; in the web form a mainnet address drops its
 * `kaspa:` prefix (the app writes it without, so the link reads cleanly) and a missing prefix
 * means mainnet.
 *
 * Links written before kachat.app used kachat.duckdns.org. Those are still opened; they are
 * never written.
 */
sealed class KaChatLinkRef {
    /** Someone's KaChat profile. Always a full, valid address. */
    data class Profile(val address: String) : KaChatLinkRef()
    /** A request to be paid: opens Send Kaspa with the address, amount and memo filled in - or,
     *  your own, shows it to copy or share again (iOS c6851d6, 1fe8388). Every field validated,
     *  and a signed one only when its signature checks out. */
    data class PaymentRequest(val request: com.kachat.app.models.PaymentRequest) : KaChatLinkRef()
}

/** [raw] is the exact link substring as it appears in the text, [range] where it sits in it. */
data class KaChatLinkMatch(val range: IntRange, val raw: String, val ref: KaChatLinkRef)

object KaChatLink {
    /** The only host the app ever writes into a share. */
    const val WEB_HOST = "kachat.app"

    /** Hosts from before kachat.app: still opened, never written. */
    val LEGACY_WEB_HOSTS = setOf("kachat.duckdns.org")

    /** The one builder the share buttons use, so the links cannot drift apart:
     *  kachat.app/u/<address>, trimmed and lowercased (iOS 44d5fa4). */
    fun profileWebUrl(address: String) =
        "https://$WEB_HOST/u/${address.trim().lowercase().removePrefix("kaspa:")}"

    // The trailing segment deliberately excludes '/', '?' and '#' so a link can never carry a
    // second path component, a query string or a fragment into the app.
    private val LINK_REGEX = Regex(
        """(?:kachat://(profile)/|https?://(?:kachat\.app|kachat\.duckdns\.org)/(u)/)([^\s/?#]+)""",
        RegexOption.IGNORE_CASE
    )
    // A payment request carries a query, so it has its own pattern: everything up to whitespace
    // or a quote/angle bracket, as iOS matches it (the memo inside is percent-encoded).
    private val PAY_LINK_REGEX = Regex(
        """(?:kachat://pay/|https?://(?:www\.)?(?:kachat\.app|kachat\.duckdns\.org)/pay/)([^\s<>"']+)""",
        RegexOption.IGNORE_CASE
    )
    private val TRAILING_PUNCTUATION = setOf('.', ',', '!', '?', ';', ':', '\'', '"', ')', ']', '}', '>')

    /** KaPay's own schemes, read as kachat:// (iOS wallet d9bec97): kapay:// is what KaPost
     *  hands its payment requests over with (kachat:// opens KaChat), kachatwallet:// its name
     *  from before KaPay. */
    private val WALLET_SCHEME_REGEX = Regex("^(?:kapay|kachatwallet)://", RegexOption.IGNORE_CASE)

    /** kachat://, kapay:// or kachatwallet:// - the custom schemes a link can open the app with. */
    fun isAppScheme(scheme: String?): Boolean =
        scheme != null && scheme.lowercase() in setOf("kachat", "kapay", "kachatwallet")

    /** True when [raw] is nothing but one of these links (no surrounding text). */
    fun parse(raw: String): KaChatLinkRef? {
        val trimmed = raw.trim().replaceFirst(WALLET_SCHEME_REGEX, "kachat://")
        PAY_LINK_REGEX.matchEntire(trimmed)?.let { return paymentRequestRef(it.groupValues[1]) }
        val match = LINK_REGEX.matchEntire(trimmed) ?: return null
        return refFor(match)
    }

    /** The first valid KaChat link in [text], or null. Invalid ones are skipped, not accepted. */
    fun findFirst(text: String): KaChatLinkMatch? {
        // Neither form can appear without "kachat" - the cheap bail-out for nearly every text.
        if (!text.contains("kachat", ignoreCase = true)) return null
        // Both patterns, in the order the links appear in the text (iOS scans with one).
        val candidates = (LINK_REGEX.findAll(text) + PAY_LINK_REGEX.findAll(text)).sortedBy { it.range.first }
        for (match in candidates) {
            var end = match.range.last
            while (end >= match.range.first && text[end] in TRAILING_PUNCTUATION) end--
            if (end < match.range.first) continue
            val range = match.range.first..end
            val raw = text.substring(range.first, range.last + 1)
            val ref = parse(raw) ?: continue
            return KaChatLinkMatch(range, raw, ref)
        }
        return null
    }

    private fun refFor(match: MatchResult): KaChatLinkRef? {
        val segment = try {
            android.net.Uri.decode(match.groupValues[3])
        } catch (e: Exception) {
            null
        } ?: return null
        return profileAddress(segment)?.let(KaChatLinkRef::Profile)
    }

    /** A payment request's path tail (`<address>?<query>`, any `#fragment` ignored): exactly one
     *  address segment, then [com.kachat.app.models.PaymentRequest.fromLink] checks the rest. */
    private fun paymentRequestRef(tail: String): KaChatLinkRef? {
        val withoutFragment = tail.substringBefore('#')
        val path = withoutFragment.substringBefore('?')
        val query = if ('?' in withoutFragment) withoutFragment.substringAfter('?') else null
        if (path.isEmpty() || '/' in path) return null
        val address = runCatching { android.net.Uri.decode(path) }.getOrNull() ?: return null
        return com.kachat.app.models.PaymentRequest.fromLink(address, query)?.let(KaChatLinkRef::PaymentRequest)
    }

    /** A linked address is attacker-controlled like everything else in a link: it has to be a
     *  valid Kaspa address, checksum and all, or the link is not ours. It opens on the network the
     *  app runs on: a link without a prefix (or shared from the other network) names the same
     *  key, so it becomes that key's address here (iOS ce20e87, IOS-003). */
    fun profileAddress(raw: String): String? {
        val trimmed = raw.trim()
        if (trimmed.length > 100) return null
        return runCatching { com.kachat.app.util.KaspaAddress.onActiveNetwork(trimmed) }.getOrNull()
    }
}
