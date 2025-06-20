package com.example.eventdicoding.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.eventdicoding.data.local.entity.EventEntity

@Dao
interface EventDao {
    @Query("SELECT * FROM event WHERE id = :id LIMIT 1")
    suspend fun getEventbyId(id:  String): EventEntity?

    @Query("SELECT * FROM event WHERE isActive = :active AND name LIKE '%'  || :query || '%'ORDER BY date(beginTime) ASC")
    fun searchEvent(active: Int, query: String): LiveData<List<EventEntity>>

    @Query("SELECT * FROM event WHERE isActive  = 0 ORDER BY date(beginTime) DESC")
    suspend fun  getInactiveEvent(): List<EventEntity>

    @Query("SELECT * FROM event  WHERE isActive = 1 ORDER BY date(beginTime) ASC")
    suspend fun getActiveEvent(): List<EventEntity>

    @Query("SELECT * FROM  event WHERE isFavorited = 1")
    fun getFavoriteEvent(): LiveData<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun  insertEvent(event: List<EventEntity>)

    @Query("UPDATE event SET isFavorited = 0 WHERE id = :id")
    suspend fun removeFavoriteFlag(id: String)

    @Query("UPDATE event SET isFavorited = 1 WHERE id = :id")
    suspend fun  addFavoriteFlag(id: String)

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Query("DELETE FROM event WHERE isActive = 0")
    suspend fun deleteInactiveEvents()

    @Query("DELETE FROM event WHERE isActive = 1")
    suspend fun deleteActiveEvents()

    @Delete
    suspend fun  deleteFavoriteEvent(event: EventEntity)

    @Query("SELECT EXISTS(SELECT * FROM event WHERE id = :id AND isFavorited = 1)")
    suspend fun isFavorited(id: String): Boolean

    @Query("SELECT * FROM event WHERE isActive = 1 AND date(beginTime)>= :currentTime ORDER BY date(beginTime) ASC LIMIT 1")
    suspend fun  getNearestActiveEvent(currentTime: Long): EventEntity?

}