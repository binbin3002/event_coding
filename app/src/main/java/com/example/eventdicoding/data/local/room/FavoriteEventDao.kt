package com.example.eventdicoding.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.eventdicoding.data.local.entity.FavoriteEventEntity

@Dao
interface FavoriteEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun  addFavoriteEvent(favoriteEvent: FavoriteEventEntity)

    @Delete
    suspend fun deleteFavoriteEvent(favoriteEvent: FavoriteEventEntity)

    @Query("SELECT * FROM `favorite_event ` ")
    fun getFavoriteEvents(): LiveData<List<FavoriteEventEntity>>

    @Query("DELETE FROM `favorite_event ` WHERE id = :eventId ")
    suspend fun  deleteFavoriteEventById(eventId: String)

}