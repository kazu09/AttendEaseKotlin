package com.kazu.attendease.activity

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
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
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    /** binding */
    private lateinit var binding: ActivityMainBinding

    /** viewModel */
    private val viewModel: MainViewModel by viewModels {
        val db = Room.databaseBuilder(
            applicationContext,
            AttendanceDatabase::class.java,
            Constants.DATABASE_NAME
        ).build()
        val dao = db.attendanceDao()
        val repository = AttendanceRepository(dao)
        MainViewModelFactory(repository)
    }

    /** userId */
    private val userId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        binding.dateTime.text = viewModel.getDateTime()
        setupObservers()
        clickListener()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getLastRecord(userId)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                showRadioButtonDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(updateBaseLocale(newBase))
    }

    /**
     * Init layout.
     */
    private fun initView() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        toolbar()
    }

    private fun toolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""
    }

    /**
     * Setting button.
     */
    private fun clickListener() {

        binding.attendance.setOnClickListener {
            binding.dateTime.text = viewModel.getDateTime()
            viewModel.onCheckInClicked(userId, viewModel.getDate())
        }

        binding.leaving.setOnClickListener {
            binding.dateTime.text = viewModel.getDateTime()
            viewModel.onCheckOutClicked(userId, viewModel.getDate())
        }

        binding.download.setOnClickListener {
            viewModel.getAllRecord()
        }
    }

    /**
     * Setup Livedata.
     */
    private fun setupObservers() {
        viewModel.checkInStatus.observe(this, Observer { result ->
            when (result) {
                is DbResult.Success -> {
                    Toast.makeText(this, getString(R.string.attendance_toast), Toast.LENGTH_SHORT)
                        .show()
                    binding.attendance.isEnabled = false
                }

                is DbResult.Error -> {
                    Toast.makeText(this, getString(R.string.db_error), Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.checkOutStatus.observe(this, Observer { result ->
            when (result) {
                is DbResult.Success -> {
                    Toast.makeText(this, getString(R.string.leaving_toast), Toast.LENGTH_SHORT)
                        .show()
                    binding.leaving.isEnabled = false
                }

                is DbResult.Error -> {
                    Toast.makeText(this, getString(R.string.db_error), Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.lastRecord.observe(this) { result ->
            when (result) {
                is DbResult.Success -> {
                    val record = result.data
                    if (record != null) {
                        binding.attendance.isEnabled = record.date != viewModel.getDate()
                        binding.leaving.isEnabled =
                            !(record.date == viewModel.getDate() && record.timeOut != null)
                    }
                }
                is DbResult.Error -> {

                }
            }
        }

        viewModel.allRecord.observe(this) { result ->
            when (result) {
                is DbResult.Success -> {
                    val record = result.data
                    // Settings download file name.
                    val file = File(this.getExternalFilesDir(null), "AttendEase.txt")
                    try {
                        file.printWriter().use { out ->
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            record.forEach { attendance ->
                                val checkInDateStr = attendance.timeIn?.let {
                                    Date(
                                        it
                                    )
                                }?.let { dateFormat.format(it) }
                                var text = "date: ${attendance.date}, Begin: $checkInDateStr"
                                attendance.timeOut?.let {
                                    val checkOutDateStr = dateFormat.format(Date(it))
                                    text += ", Finish: $checkOutDateStr"
                                }
                                // Output text
                                out.println(text)
                                Toast.makeText(this, getString(R.string.output_text_success), Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (_: IOException) {
                        Toast.makeText(this, getString(R.string.output_text_failed), Toast.LENGTH_SHORT).show()
                    }
                }
                is DbResult.Error -> {
                    Toast.makeText(this, getString(R.string.output_text_failed), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Localize setting dialog.
     * default Japanese.
     */
    private fun showRadioButtonDialog() {
        val items = arrayOf("ja", "en")
        // Keep item index (initial value = -1)
        var checkedItem = -1

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.set_lang_dialog_title))
            .setSingleChoiceItems(items, checkedItem) { _, which ->
                checkedItem = which

            }.setPositiveButton(getString(R.string.set_lang_dialog_ok)) { _, _ ->
                if (checkedItem == -1) {
                    return@setPositiveButton
                }
                when (items[checkedItem]) {
                    Constants.EN_CODE -> {
                        Constants.languageCode = "en"
                        updateBaseLocale(this)
                        recreate()
                    }

                    Constants.JA_CODE -> {
                        Constants.languageCode = "ja"
                        updateBaseLocale(this)
                        recreate()
                    }

                    else -> {

                    }
                }
            }
            .setNegativeButton(getString(R.string.set_lang_dialog_cancel), null)
            .show()
    }

    /**
     * Update Locale.
     * @param context context
     */
    private fun updateBaseLocale(context: Context): Context? {
        val locale = Locale(Constants.languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

}