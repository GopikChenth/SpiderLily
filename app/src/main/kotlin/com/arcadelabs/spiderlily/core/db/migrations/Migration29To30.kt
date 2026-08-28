package com.arcadelabs.spiderlily.core.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.arcadelabs.spiderlily.core.db.TABLE_DOWNLOAD_QUEUE
import com.arcadelabs.spiderlily.core.db.TABLE_SMART_DOWNLOADS
import com.arcadelabs.spiderlily.core.db.TABLE_TAGS

class Migration29To30 : Migration(29, 30) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `$TABLE_DOWNLOAD_QUEUE` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `manga_id` INTEGER NOT NULL,
                `chapters_ids` TEXT NOT NULL,
                `priority` INTEGER NOT NULL,
                `created_at` INTEGER NOT NULL,
                `wifi_only` INTEGER NOT NULL,
                `charging_only` INTEGER NOT NULL,
                `off_peak_only` INTEGER NOT NULL,
                `is_paused` INTEGER NOT NULL,
                FOREIGN KEY(`manga_id`) REFERENCES `manga`(`manga_id`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_download_queue_manga_id` ON `$TABLE_DOWNLOAD_QUEUE` (`manga_id`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_download_queue_priority` ON `$TABLE_DOWNLOAD_QUEUE` (`priority`)")

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `$TABLE_SMART_DOWNLOADS` (
                `manga_id` INTEGER NOT NULL,
                `downloaded_indices` TEXT NOT NULL,
                `current_index` INTEGER NOT NULL,
                PRIMARY KEY(`manga_id`),
                FOREIGN KEY(`manga_id`) REFERENCES `manga`(`manga_id`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_tags_title` ON `$TABLE_TAGS` (`title`)")
    }
}
