package com.example.eventdicoding.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.eventdicoding.R
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.eventdicoding.databinding.ActivityMainBinding
import com.example.eventdicoding.ui.notification.DailyReminderWorker
import com.example.eventdicoding.ui.setting.SettingPreferences
import com.example.eventdicoding.ui.setting.dataStore
import com.example.eventdicoding.ui.viewmodel.MainViewModel
import com.example.eventdicoding.ui.viewmodel.ViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
 private  lateinit var  navController: NavController
 private lateinit var  binding: ActivityMainBinding
 private  lateinit var  appBarConfiguration: AppBarConfiguration
 private lateinit var  toolbar: Toolbar
 private  val viewModel: MainViewModel by viewModels{
     ViewModelFactory.getInstance(applicationContext, SettingPreferences.getInstance(applicationContext.dataStore))
 }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleDailyReminder()

        binding = ActivityMainBinding.inflate((layoutInflater))
        setContentView(binding.root)

        toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController


        val bottomNavView : BottomNavigationView = findViewById(R.id.nav_view)
        bottomNavView.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(
        setOf(R.id.navigation_home, R.id.navigation_upcoming, R.id.navigation_finished, R.id.navigation_favorite, R.id.navigation_setting)
        )
        toolbar.setupWithNavController(navController, appBarConfiguration)

        viewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive){
                setDefaultNightMode(MODE_NIGHT_YES)


            }else{
                setDefaultNightMode(MODE_NIGHT_NO)
            }
        }
    }
    private  fun scheduleDailyReminder(){
        val workRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(1,TimeUnit.DAYS)
            .build()
        WorkManager.getInstance(this).enqueue(workRequest)
    }
}
