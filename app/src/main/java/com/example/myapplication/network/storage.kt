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
    @PrimaryKey val id: String,
    @ColumnInfo(name = "quote") val quote: String?,
    @ColumnInfo(name= "author") val author:String
)

@Dao
interface QuoteDao {
    @Query("SELECT * FROM quotelocal limit 20")
    fun getAll(): List<QuoteLocal>

    @Insert
    fun insertQuote( users: List<QuoteLocal>)

    @Query("Delete from QuoteLocal")
    fun deleteQuotes()

}

@Database(entities = [QuoteLocal::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun quoteDao(): QuoteDao
}