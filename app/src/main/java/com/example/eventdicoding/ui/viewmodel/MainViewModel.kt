package com.example.eventdicoding.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import com.example.eventdicoding.data.EventRepository
import com.example.eventdicoding.data.local.entity.EventEntity
import com.example.eventdicoding.data.local.entity.FavoriteEventEntity
import com.example.eventdicoding.ui.notification.DailyReminderWorker
import com.example.eventdicoding.ui.setting.SettingPreferences
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainViewModel(private  val pref: SettingPreferences, private  val eventRespository: EventRepository, private val workManager: androidx.work.WorkManager) : ViewModel() {

    private  val _isReminderEnabled = MutableLiveData<Boolean>()
    val isReminderEnabled: LiveData<Boolean> = _isReminderEnabled

    init {
        viewModelScope.launch{
            pref.getReminderPreference().collect{
                _isReminderEnabled.value = it
            }
        }
    }

    fun fetchActiveEvents() = eventRespository.getActiveEvent()
    fun fetchInactiveEvents() = eventRespository.getInactiveEvent()
    fun searchEvents(active: Int, query: String) = eventRespository.searchEvent(active, query)
    fun getDetailEvent(id: String) = eventRespository.getEvent(id)
    fun fetchFavoritedEvents() : LiveData<List<EventEntity>> {
        return eventRespository.getFavoritedEvent()
    }






    fun updateFavoriteStatus(event: EventEntity, favoriteState: Boolean){
        viewModelScope.launch{
            setFavoriteEvent(event, favoriteState)
        }
    }


    private  suspend fun  setFavoriteEvent(event: EventEntity,favoriteState: Boolean){
        eventRespository.setFavoriteEvent(event, favoriteState)
    }

     fun getThemeSettings() : LiveData<Boolean>{
         return  pref.getThemeSetting().asLiveData()
     }
    fun saveThemeSetting(isDarkModeActive: Boolean){
        viewModelScope.launch{
            pref.saveThemeSetting(isDarkModeActive)
        }
    }
     fun toggleReminder(enabled: Boolean){
         viewModelScope.launch{
             pref.saveReminderPreference(enabled)
             _isReminderEnabled.value = enabled
             if (enabled){
                 scheduleReminder()
             }else{
                 cancelReminder()
             }
         }
     }
    private  fun scheduleReminder(){
        val reminderRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
            .build()
        workManager.enqueueUniquePeriodicWork(
            REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            reminderRequest
        )
    }
    private  fun cancelReminder(){
        workManager.cancelUniqueWork(REMINDER_WORK_NAME)
    }
    companion object{
        private  const val  REMINDER_WORK_NAME = "daily_event_reminder"
    }
}