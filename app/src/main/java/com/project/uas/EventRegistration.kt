package com.project.uas

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventRegistration(
    @SerializedName("event_id")
    var eventId: Int,
    @SerializedName("event_title")
    var eventTitle: String,
    @SerializedName("student_name")
    var studentName: String,
    @SerializedName("nrp")
    var nrp: String,
    @SerializedName("faculty")
    var faculty: String,
    @SerializedName("attendance_mode")
    var attendanceMode: String,
    @SerializedName("extras")
    var extras: String,
    @SerializedName("preferred_date")
    var preferredDate: String,
    @SerializedName("notes")
    var notes: String
) : Parcelable {
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        p0.writeInt(eventId)
        p0.writeString(eventTitle)
        p0.writeString(studentName)
        p0.writeString(nrp)
        p0.writeString(faculty)
        p0.writeString(attendanceMode)
        p0.writeString(extras)
        p0.writeString(preferredDate)
        p0.writeString(notes)
    }
}
