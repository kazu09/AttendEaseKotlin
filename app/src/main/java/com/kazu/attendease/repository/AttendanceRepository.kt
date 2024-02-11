package com.kazu.attendease.repository

import com.kazu.attendease.db.dao.AttendanceDao
import com.kazu.attendease.db.entity.AttendanceRecord
import com.kazu.attendease.utils.DbResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AttendanceRepository(private val dao: AttendanceDao) {
    /**
     * Insert start data at tap.
     *
     * @param userId userId.
     * @param date Current date.
     * @param timeIn Current time.
     * @return Unit
     */
    suspend fun checkIn(userId: Int, date: String, timeIn: Long): DbResult<Unit> = withContext(
        Dispatchers.IO) {
        return@withContext try {
            val record = AttendanceRecord(userId = userId, date = date, timeIn = timeIn)
            dao.insertRecord(record)
            DbResult.Success(Unit)
        } catch (e: Exception) {
            // When saving the database failed.
            DbResult.Error(e)
        }
    }

    /**
     * Update timeOut time at tap.
     * @param userId userId.
     * @param date Current date.
     * @param timeOut Current time.
     * @return Unit
     */
    suspend fun checkOut(userId: Int, date: String, timeOut: Long): DbResult<Unit> = withContext(Dispatchers.IO){
        return@withContext try {
            val record = dao.getRecordForUserAndDate(userId, date)
            record?.let {
                it.timeOut = timeOut
                dao.updateRecord(it)
            }
            DbResult.Success(Unit)
        } catch (e: java.lang.Exception) {
            // When saving the database failed.
            DbResult.Error(e)
        }
    }
}