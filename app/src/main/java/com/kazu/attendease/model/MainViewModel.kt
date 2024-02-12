/**
 * MainViewModel.kt
 * AttendEase
 *
 * Created by kazu 2024.
 */
package com.kazu.attendease.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazu.attendease.db.entity.AttendanceRecord
import com.kazu.attendease.repository.AttendanceRepository
import com.kazu.attendease.utils.DbResult
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainViewModel(private val repository: AttendanceRepository) : ViewModel() {
    private val _checkInStatus = MutableLiveData<DbResult<Unit>>()
    val checkInStatus: LiveData<DbResult<Unit>> = _checkInStatus

    private val _checkOutStatus = MutableLiveData<DbResult<Unit>>()
    val checkOutStatus: LiveData<DbResult<Unit>> = _checkOutStatus

    private val _lastRecord = MutableLiveData<DbResult<AttendanceRecord?>>()
    val lastRecord: LiveData<DbResult<AttendanceRecord?>> = _lastRecord

    private val _allRecord = MutableLiveData<DbResult<List<AttendanceRecord>>>()
    val allRecord: LiveData<DbResult<List<AttendanceRecord>>> = _allRecord

    fun onCheckInClicked(userId: Int, date: String) {
        viewModelScope.launch {
            try {
                val result = repository.checkIn(userId, date, System.currentTimeMillis())
                _checkInStatus.postValue(result)
            } catch (e: Exception) {
                _checkInStatus.postValue(DbResult.Error(e))
            }
        }
    }

    fun onCheckOutClicked(userId: Int, date: String) {
        viewModelScope.launch {
            try {
                val result = repository.checkOut(userId, date, System.currentTimeMillis())
                _checkOutStatus.postValue(result)
            } catch (e: Exception) {
                _checkOutStatus.postValue(DbResult.Error(e))
            }
        }
    }

    /**
     * Get last record.
     * @param userId userId
     */
    fun getLastRecord(userId: Int) {
        viewModelScope.launch {
            try {
                val result = repository.getLastRecord(userId)
                _lastRecord.value = result
            } catch (e: Exception) {
                _lastRecord.postValue(DbResult.Error(e))
            }
        }
    }

    /**
     * Get All Record.
     */
    fun getAllRecord() {
        viewModelScope.launch {
            try {
                val result = repository.getAllRecord()
                _allRecord.value = result
            } catch (e: Exception) {
                _allRecord.postValue(DbResult.Error(e))
            }
        }
    }

    /**
     * Get current date and current time.
     * @return "yyyy/mm/dd hh:mm:ss"
     */
    fun getDateTime(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
        return currentDateTime.format(formatter)
    }

    /**
     * get current date.
     * @return "yyyy-MM-dd"
     */
    fun getDate(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return currentDateTime.format(formatter)
    }

}