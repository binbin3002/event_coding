package com.example.eventdicoding.ui.setting

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.eventdicoding.databinding.FragmentSettingBinding
import com.example.eventdicoding.ui.viewmodel.MainViewModel
import com.example.eventdicoding.ui.viewmodel.ViewModelFactory
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingFragment : Fragment (){
    private  var _binding : FragmentSettingBinding? = null
    private  val binding get() = _binding!!
    private lateinit var  switchTheme: SwitchMaterial
    private  lateinit var  switchReminder: SwitchMaterial
    private  lateinit var  pref: SettingPreferences
    private  lateinit var  notificationPermissionLauncher: ActivityResultLauncher<String>

    private  val mainViewModel: MainViewModel by viewModels{
        ViewModelFactory.getInstance(requireContext(), pref)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return  binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){isGranted: Boolean ->
            if (isGranted){
                mainViewModel.toggleReminder(true)

            }else{
                Toast.makeText(requireContext(),"Notification permission denied", Toast.LENGTH_SHORT).show()
                switchReminder.isChecked = false
            }
        }
        switchTheme = binding.switchTheme
        switchReminder = binding.switchReminder
        pref = SettingPreferences.getInstance(requireContext().applicationContext.dataStore)

        setupThemeSwitch()
        setupReminderSwitch()
    }

    private  fun setupThemeSwitch() {
        mainViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                switchTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                switchTheme.isChecked = false
            }
        }
        switchTheme.setOnCheckedChangeListener{ _: CompoundButton, isChecked: Boolean->
            mainViewModel.saveThemeSetting(isChecked)
        }
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun setupReminderSwitch(){
        mainViewModel.isReminderEnabled.observe(viewLifecycleOwner){isReminderEnabled ->
            switchReminder.isChecked = isReminderEnabled
        }
        switchReminder.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked){
                requestNotificationPermission()
            }else{
                mainViewModel.toggleReminder(false)
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission(){
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }else{
            mainViewModel.toggleReminder(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}