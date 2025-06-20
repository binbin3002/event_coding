package com.example.eventdicoding.data.local.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_event ")
data class FavoriteEventEntity(
    @field:PrimaryKey
    val id: String,
    val name: String,
    val summary: String,
    val description: String,
    val imageLogo: String? = null,
    val mediaCover: String? = null,
    val category: String,
    val ownerName: String,
    val cityName: String,
    val quota: Int,
    val registrants: Int,
    val beginTime: String,
    val endTime: String,
    val link:  String? = null





)
