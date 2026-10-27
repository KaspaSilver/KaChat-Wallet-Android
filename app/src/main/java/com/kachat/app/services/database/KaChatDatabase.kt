package com.kachat.app.services.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kachat.app.models.PortfolioEntity
import com.kachat.app.models.PortfolioTransactionEntity

/**
 * Room database - the Portfolio's transactions and portfolio definitions, nothing else. The
 * message, contact, group, broadcast and swap tables went with the chats (iOS wallet 301ebfb);
 * the Address Book lives in SharedPreferences (AddressBookManager).
 */
@Database(
    entities = [
        PortfolioTransactionEntity::class,
        PortfolioEntity::class,
    ],
    version = 43,
    exportSchema = true
)
abstract class KaChatDatabase : RoomDatabase() {
    abstract fun portfolioDao(): PortfolioDao
    abstract fun portfolioDefinitionDao(): PortfolioDefinitionDao

    companion object {
        /**
         * v42 -> v43: drops the chat tables, leaving the two Portfolio tables exactly as they
         * were - so an update keeps the Portfolio rather than rebuilding the database empty
         * (iOS wallet 301ebfb).
         */
        val MIGRATION_42_43 = object : Migration(42, 43) {
            override fun migrate(db: SupportSQLiteDatabase) {
                for (table in CHAT_TABLES) db.execSQL("DROP TABLE IF EXISTS `$table`")
            }
        }

        private val CHAT_TABLES = listOf(
            "messages", "contacts", "broadcast_channels", "broadcast_messages",
            "broadcast_hidden_senders", "deleted_contacts", "message_sync_cursors",
            "swap_transactions", "groups", "group_messages", "group_sync_cursors", "reactions",
            "message_edits",
        )
    }
}
