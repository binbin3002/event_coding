package com.example.eventdicoding.data.local.entity

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

@Entity(tableName = "event")
class EventEntity(
    @field:ColumnInfo(name = "id")
    @field:PrimaryKey
    val id: String,

    @field:ColumnInfo(name = "name")
    val name: String,

    @field:ColumnInfo(name = "summary")
    val summary: String,


    @field:ColumnInfo(name = "description")
    val description: String,

    @field:ColumnInfo(name = "imageLogo")
    val imageLogo: String? = null,

    @field:ColumnInfo(name = "mediaCover")
    val mediaCover: String? = null,

    @field:ColumnInfo(name = "category")
    val category: String,

    @field:ColumnInfo(name = "ownerName")
    val ownerName: String,

    @field:ColumnInfo(name = "cityName")
    val cityName: String,

    @field:ColumnInfo(name = "quota")
    val quota: Int,

    @field:ColumnInfo(name = "registrants")
    val registrants: Int,

    @field:ColumnInfo(name = "beginTime")
    val beginTime: String,

    @field:ColumnInfo(name = "endTime")
    val endTime: String,

    @field:ColumnInfo(name = "link")
    val link: String? = null,

    @field:ColumnInfo(name = "isFavorited")
    var isFavorited: Boolean = false,

    @field:ColumnInfo(name = "isActive")
    val isActive: Boolean




)
