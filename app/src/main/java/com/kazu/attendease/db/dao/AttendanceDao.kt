package com.kazu.attendease.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kazu.attendease.db.entity.AttendanceRecord

@Dao
interface AttendanceDao {
    @Insert
    fun insertRecord(record: AttendanceRecord)

    @Update
    fun updateRecord(record: AttendanceRecord)

    @Query("SELECT * FROM attendance_records WHERE userId = :userId AND date = :date LIMIT 1")
    fun getRecordForUserAndDate(userId: Int, date: String): AttendanceRecord?

    @Query("SELECT * FROM attendance_records WHERE userId = :userId ORDER BY date DESC LIMIT 1")
    fun getLastRecordForUser(userId: Int): AttendanceRecord?
}