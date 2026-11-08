package com.project.uas

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CampusEvent(
    var id: Int,
    var title: String,
    var category: String,
    var date: String,
    var location: String,
    var organizer: String,
    @SerializedName("contact_phone")
    var contactPhone: String,
    @SerializedName("image_url")
    var imageUrl: String,
    var description: String,
    @SerializedName("skkk_points")
    var skkkPoints: Int,
    @SerializedName("quota")
    var quota: Int,
    @SerializedName("website_url")
    var websiteUrl: String
) : Parcelable {
    override fun toString(): String = "$title ($category)"

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        p0.writeInt(id)
        p0.writeString(title)
        p0.writeString(category)
        p0.writeString(date)
        p0.writeString(location)
        p0.writeString(organizer)
        p0.writeString(contactPhone)
        p0.writeString(imageUrl)
        p0.writeString(description)
        p0.writeInt(skkkPoints)
        p0.writeInt(quota)
        p0.writeString(websiteUrl)
    }
}
