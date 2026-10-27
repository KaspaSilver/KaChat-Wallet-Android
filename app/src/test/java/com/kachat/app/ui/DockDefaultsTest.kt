package com.kachat.app.ui

import org.junit.Assert.assertEquals
import org.junit.Test

/** KaPay's dock is fixed (iOS wallet `AppTab.dock`, 040f91a): Portfolio, Storage, Wallet, .kachat,
 *  Settings - whatever arrangement an older build saved. The chat tabs and Kaspa Hub are gone
 *  (iOS wallet 301ebfb). */
class DockDefaultsTest {
    private val walletDock = listOf("portfolio", "cold_storage", "profile", "kachat_names", "settings")

    @Test
    fun `the dock is the wallet's five tabs`() {
        assertEquals(walletDock, WALLET_DOCK.map { it.route })
        assertEquals(walletDock, resolveDock(emptyList(), emptySet()).map { it.route })
    }

    @Test
    fun `a saved arrangement from KaChat does not change the dock`() {
        val saved = listOf("kaspa_hub", "portfolio", "chats", "kaposts", "profile")
        assertEquals(walletDock, resolveDock(saved, setOf("portfolio")).map { it.route })
    }

    @Test
    fun `the app opens on the Wallet tab`() {
        assertEquals("profile", HOME_ROUTE)
    }
}
