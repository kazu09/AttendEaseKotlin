/**
 * AttendanceDatabase.kt
 * AttendEase
 *
 * Created by kazu 2024.
 */
package com.kazu.attendease.db.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kazu.attendease.db.dao.AttendanceDao
import com.kazu.attendease.db.entity.AttendanceRecord

@Database(entities = [AttendanceRecord::class], version = 1)
abstract class AttendanceDatabase : RoomDatabase() {
    abstract fun attendanceDao() : AttendanceDao
}