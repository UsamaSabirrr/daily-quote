package com.example.myapplication.network

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase

@Entity
data class QuoteLocal(
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "title") val quote: String?,
)

@Dao
interface QuoteDao {
    @Query("SELECT * FROM quotelocal")
    fun getAll(): List<QuoteLocal>

    @Insert
    fun insertQuote(vararg users: QuoteLocal)

}

@Database(entities = [QuoteLocal::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun quoteDao(): QuoteDao
}