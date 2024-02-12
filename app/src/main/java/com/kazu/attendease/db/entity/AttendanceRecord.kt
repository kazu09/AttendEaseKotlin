/**
 * AttendanceRecord.kt
 * AttendEase
 *
 * Created by kazu 2024.
 */
package com.kazu.attendease.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance_records")
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val date: String, //  yyyy-MM-dd
    var timeIn: Long? = null, // UNIX time
    var timeOut: Long? = null // UNIX time
)
