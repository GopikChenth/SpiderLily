package com.arcadelabs.spiderlily.mihon.model.parcelable

import android.os.Parcel
import android.os.Parcelable
import com.arcadelabs.spiderlily.mihon.model.contentSource
import com.arcadelabs.spiderlily.mihon.parsers.model.ContentPage
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler

object ContentPageParceler : Parceler<ContentPage> {
	override fun create(parcel: Parcel) = ContentPage(
		id = parcel.readLong(),
		url = requireNotNull(parcel.readString()),
		preview = parcel.readString(),
		source = contentSource(parcel.readString()),
	)

	override fun ContentPage.write(parcel: Parcel, flags: Int) {
		parcel.writeLong(id)
		parcel.writeString(url)
		parcel.writeString(preview)
		parcel.writeString(source.name)
	}
}

@Parcelize
@TypeParceler<ContentPage, ContentPageParceler>
class ParcelableContentPage(val page: ContentPage) : Parcelable

