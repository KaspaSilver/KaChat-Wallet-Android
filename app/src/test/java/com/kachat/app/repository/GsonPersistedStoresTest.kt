package com.kachat.app.repository

import com.kachat.app.util.KaspaNetwork
import com.kachat.app.util.PersistedJson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.reflect.Modifier

/**
 * The Gson store audit AND-001 found unkept by R8 (the other, ChatRequestStore, went with the
 * chats): it round-trips, its known-field list
 * matches the class (so a new field cannot be mistaken for a renamed one), and a record a
 * minified build wrote with renamed fields is refused rather than misread.
 */
class GsonPersistedStoresTest {

    private fun instanceFieldNames(type: Class<*>): Set<String> =
        type.declaredFields.filter { !Modifier.isStatic(it.modifiers) && !it.isSynthetic }.map { it.name }.toSet()

    // ConnectionProfile

    @Test
    fun `connection profile round-trips`() {
        val custom = ConnectionProfile(
            indexerUrl = "https://indexer.example",
            kapostIndexerUrl = "",
            broadcastIndexerUrl = null,
            pushIndexerUrl = "https://push.example",
            translationServiceUrl = "https://tr.example",
            kaspaRestUrl = "https://rest.example",
            trustedNodeAddress = "node.example:16110",
            savedNodeAddresses = "[]",
        )
        assertEquals(custom, ConnectionProfile.decode(ConnectionProfile.encode(custom)))
        val testnet = ConnectionProfile.defaults(KaspaNetwork.Type.TESTNET)
        assertEquals(testnet, ConnectionProfile.decode(ConnectionProfile.encode(testnet)))
    }

    @Test
    fun `connection profile field list matches the class`() {
        assertEquals(instanceFieldNames(ConnectionProfile::class.java), ConnectionProfile.FIELDS)
    }

    @Test
    fun `connection profile written with renamed fields is refused`() {
        val obfuscated = """{"a":"https://indexer.example","b":"","e":"https://tr.example"}"""
        assertTrue(ConnectionProfile.isForeignRecord(obfuscated))
        assertNull(ConnectionProfile.decode(obfuscated))
    }

    @Test
    fun `connection profile with every field null reads as the empty profile`() {
        assertFalse(ConnectionProfile.isForeignRecord("{}"))
        assertEquals(ConnectionProfile(), ConnectionProfile.decode("{}"))
    }

    // PersistedJson

    @Test
    fun `only an object with an unknown key counts as foreign`() {
        val known = setOf("x", "y")
        assertFalse(PersistedJson.hasForeignFields("""{"x":1}""", known))
        assertTrue(PersistedJson.hasForeignFields("""{"x":1,"a":2}""", known))
        assertFalse(PersistedJson.hasForeignFields("[1,2]", known))
        assertFalse(PersistedJson.hasForeignFields("not json {", known))
    }
}
