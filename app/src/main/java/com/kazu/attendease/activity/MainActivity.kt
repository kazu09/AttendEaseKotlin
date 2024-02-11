package com.kazu.attendease.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.room.Room
import com.kazu.attendease.R
import com.kazu.attendease.common.Constants
import com.kazu.attendease.databinding.ActivityMainBinding
import com.kazu.attendease.db.room.AttendanceDatabase
import com.kazu.attendease.factory.MainViewModelFactory
import com.kazu.attendease.model.MainViewModel
import com.kazu.attendease.repository.AttendanceRepository
import com.kazu.attendease.utils.DbResult

class MainActivity : AppCompatActivity() {
    /** binding */
    private lateinit var binding: ActivityMainBinding

    /** viewModel */
    private val viewModel: MainViewModel by viewModels {
        val db = Room.databaseBuilder(applicationContext, AttendanceDatabase::class.java, Constants.DATABASE_NAME).build()
        val dao = db.attendanceDao()
        val repository = AttendanceRepository(dao)
        MainViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        binding.dateTime.text = viewModel.getDateTime()
        setupObservers()
        clickListener()
    }

    /**
     * Init layout.
     */
    private fun initView() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    /**
     * Setting button.
     */
    private fun clickListener() {

        binding.attendance.setOnClickListener {
            binding.dateTime.text = viewModel.getDateTime()
            viewModel.onCheckInClicked(1, viewModel.getDate())
        }

        binding.leaving.setOnClickListener {
            binding.dateTime.text = viewModel.getDateTime()
            viewModel.onCheckOutClicked(1, viewModel.getDate())
        }
    }

    /**
     * Setup Livedata.
     */
    private fun setupObservers() {
        viewModel.checkInStatus.observe(this, Observer { result ->
            when (result) {
                is DbResult.Success -> {
                    Toast.makeText(this, getString(R.string.attendance_toast), Toast.LENGTH_SHORT).show()
//                    binding.attendance.isEnabled = false
                }
                is DbResult.Error -> {
                    Toast.makeText(this, getString(R.string.db_error), Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.checkOutStatus.observe(this, Observer { result ->
            when (result) {
                is DbResult.Success -> {
                    Toast.makeText(this, getString(R.string.leaving_toast), Toast.LENGTH_SHORT).show()
//                    binding.leaving.isEnabled = false
                }
                is DbResult.Error -> {
                    Toast.makeText(this, getString(R.string.db_error), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}