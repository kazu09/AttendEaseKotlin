/**
 * DbResult.kt
 * AttendEase
 *
 * Created by kazu 2024.
 */
package com.kazu.attendease.utils

import java.lang.Exception

sealed class DbResult<out T> {
    data class Success<out T>(val data: T) : DbResult<T>()
    data class Error(val exception: Exception) : DbResult<Nothing>()
}