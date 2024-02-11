package com.kazu.attendease

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.kazu.attendease.databinding.ActivityMainBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    /** binding */
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setDateTime()
        clickListener()
    }

    /**
     * Setting button.
     */
    private fun clickListener() {
        binding.attendance.setOnClickListener {
            Toast.makeText(this, getString(R.string.attendance_toast), Toast.LENGTH_SHORT).show()
            setDateTime()
            binding.attendance.isEnabled = false
        }

        binding.leaving.setOnClickListener {
            Toast.makeText(this, getString(R.string.leaving_toast), Toast.LENGTH_SHORT).show()
            setDateTime()
            binding.leaving.isEnabled = false
        }
    }

    /**
     * Setting date and time.
     */
    private fun setDateTime() {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
        val dateTime = currentDateTime.format(formatter)
        binding.dateTime.text = dateTime
    }
}