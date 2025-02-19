package com.example.myapplication.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase

@Entity
data class QuoteEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "quote") val quote: String?,
    @ColumnInfo(name= "author") val author:String
)

@Dao
interface QuoteDao {
    @Query("SELECT * FROM QuoteEntity limit 20")
    fun getAll(): List<QuoteEntity>

    @Insert
    fun insertQuote( users: List<QuoteEntity>)

    @Query("Delete from QuoteEntity")
    fun deleteQuotes()

}

@Database(entities = [QuoteEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun quoteDao(): QuoteDao
}