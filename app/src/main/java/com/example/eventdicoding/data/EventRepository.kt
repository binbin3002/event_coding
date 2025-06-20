package com.example.eventdicoding.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.example.eventdicoding.data.local.entity.EventEntity
import com.example.eventdicoding.data.local.entity.FavoriteEventEntity
import com.example.eventdicoding.data.local.room.EventDao
import com.example.eventdicoding.data.local.room.FavoriteEventDao
import com.example.eventdicoding.data.remote.retrofit.ApiService

class EventRepository private  constructor(
    private  val apiService: ApiService,
    private val eventDao: EventDao,
    private val favoriteEventDao: FavoriteEventDao
){

    suspend fun  setFavoriteEvent(event: EventEntity, favoriteState: Boolean){
        if (favoriteState){
            val favoriteEvent = FavoriteEventEntity(
                event.id,
                event.name,
                event.summary,
                event.description,
                event.imageLogo,
                event.mediaCover,
                event.category,
                event.ownerName,
                event.cityName,
                event.quota,
                event.registrants,
                event.beginTime,
                event.endTime,
                event.link
            )
            favoriteEventDao.addFavoriteEvent(favoriteEvent)
            eventDao.addFavoriteFlag(event.id)
        }else{

            val favoriteEvent = FavoriteEventEntity(
                event.id,
                event.name,
                event.summary,
                event.description,
                event.imageLogo,
                event.mediaCover,
                event.category,
                event.ownerName,
                event.cityName,
                event.quota,
                event.registrants,
                event.beginTime,
                event.endTime,
                event.link
            )
            favoriteEventDao.deleteFavoriteEvent(favoriteEvent)
            eventDao.removeFavoriteFlag(event.id)

        }

    }
    fun getActiveEvent(): LiveData<Result<List<EventEntity>>> = liveData{
        emit(Result.Loading)
      try{
          val localData  = eventDao.getActiveEvent()
          val response = apiService.getEvents(1)
          val events = response.listEvents

          if (localData.isNotEmpty() && localData.size == events.size && localData == events){
              emit(Result.Success(localData))
          }else{
              try{
                  val eventList = events.map { event->
                      val isFavorited = eventDao.isFavorited(event.id.toString())
                      EventEntity(
                          event.id.toString(),
                          event.name.toString(),
                          event.summary.toString(),
                          event.description.toString(),
                          event.imageLogo,
                          event.mediaCover,
                          event.category.toString(),
                          event.ownerName.toString(),
                          event.cityName.toString(),
                          event.quota!!.toInt(),
                          event.registrants!!.toInt(),
                          event.beginTime.toString(),
                          event.endTime.toString(),
                          event.link,
                          isFavorited,
                          isActive = true
                      )
                  }
                  eventDao.deleteActiveEvents()
                  eventDao.insertEvent(eventList)
                  emit(Result.Success(eventList))

              }catch (e: Exception){
                  emit(Result.Error("No internet Connection "))
              }
          }



      }catch (e: Exception){
          emit(Result.Error("An Error occured: ${e.message}"))
      }
    }

    fun getInactiveEvent(): LiveData<Result<List<EventEntity>>> = liveData{
        emit(Result.Loading)
        try{
            val localData  = eventDao.getInactiveEvent()
            val response = apiService.getEvents(0)
            val events = response.listEvents

            if (localData.isNotEmpty() && localData.size == events.size && localData == events){
                emit(Result.Success(localData))
            }else{
                try{
                    val eventList = events.map { event->
                        val isFavorited = eventDao.isFavorited(event.id.toString())
                        EventEntity(
                            event.id.toString(),
                            event.name.toString(),
                            event.summary.toString(),
                            event.description.toString(),
                            event.imageLogo,
                            event.mediaCover,
                            event.category.toString(),
                            event.ownerName.toString(),
                            event.cityName.toString(),
                            event.quota!!.toInt(),
                            event.registrants!!.toInt(),
                            event.beginTime.toString(),
                            event.endTime.toString(),
                            event.link,
                            isFavorited,
                            isActive = false
                        )
                    }
                    eventDao.deleteInactiveEvents()
                    eventDao.insertEvent(eventList)
                    emit(Result.Success(eventList))

                }catch (e: Exception){
                    emit(Result.Error("No internet Connection and no local data available "))
                }
            }
        }catch (e: Exception){
            emit(Result.Error("An Error occured: ${e.message}"))
        }
    }
     fun searchEvent(active: Int, query: String): LiveData<Result<List<EventEntity>>> = liveData{
         emit(Result.Loading)
         try{
             val localEvent = eventDao.searchEvent(active, query)
             emitSource(localEvent.map { eventList ->
                 if (eventList.isEmpty()){
                     Result.Success(emptyList())
                 }else {
                     Result.Success(eventList)
                 }
             })
         }catch (e: Exception){
             emit(Result.Error(e.message.toString()))
         }
     }


    fun getEvent(id : String): LiveData<Result<EventEntity>> = liveData{
        emit(Result.Loading)
        try {
            val localEvent = eventDao.getEventbyId(id)
            if (localEvent != null){
                emit(Result.Success(localEvent))
            }else{
                emit(Result.Error("Event not found"))
            }
        }catch (e: Exception){
            emit(Result.Error("Event not found"))
        }
    }

    fun getFavoritedEvent(): LiveData<List<EventEntity>>{
        return  eventDao.getFavoriteEvent()
    }
     suspend fun  getNearestActiveEvent(): EventEntity?{
         val currentTime  = System.currentTimeMillis()
         val nearestEvent = eventDao.getNearestActiveEvent(currentTime)
         return nearestEvent
     }
    companion object{
        @Volatile
        private  var instance: EventRepository? = null
        fun getInstance(
            apiService: ApiService,
            eventDao: EventDao,
            favoriteEventDao: FavoriteEventDao
        ): EventRepository =
            instance?: synchronized(this){
                instance?: EventRepository(apiService, eventDao, favoriteEventDao)

            }.also { instance = it }
    }
}