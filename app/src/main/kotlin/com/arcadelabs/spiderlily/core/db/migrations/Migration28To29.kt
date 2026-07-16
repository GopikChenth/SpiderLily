package com.arcadelabs.spiderlily.core.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration28To29 : Migration(28, 29) {
	override fun migrate(db: SupportSQLiteDatabase) {
		db.execSQL("ALTER TABLE sources ADD COLUMN title TEXT")
	}
}
