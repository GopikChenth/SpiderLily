package com.arcadelabs.spiderlily.tracker.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.arcadelabs.spiderlily.core.db.entity.MangaEntity
import com.arcadelabs.spiderlily.core.db.entity.MangaTagsEntity
import com.arcadelabs.spiderlily.core.db.entity.TagEntity

class TrackLogWithManga(
	@Embedded val trackLog: TrackLogEntity,
	@Relation(
		parentColumn = "manga_id",
		entityColumn = "manga_id"
	)
	val manga: MangaEntity,
	@Relation(
		parentColumn = "manga_id",
		entityColumn = "tag_id",
		associateBy = Junction(MangaTagsEntity::class)
	)
	val tags: List<TagEntity>,
)