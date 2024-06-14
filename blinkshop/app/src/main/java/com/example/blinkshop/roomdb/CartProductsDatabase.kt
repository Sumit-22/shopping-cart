package com.example.blinkshop.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CartProducts::class],version = 1, exportSchema = true) //in case of true a json file is created her we don't need
abstract class CartProductsDatabase :RoomDatabase(){
     abstract fun cartProductsDaof():CartProductsDao
     companion object{

         @Volatile
         var INSTANCE: CartProductsDatabase ? = null
         fun getDatabaseInstance(context: Context): CartProductsDatabase{
             val tempInstance = INSTANCE
             if (tempInstance != null) return tempInstance

             synchronized(this){
                 val roomDb = Room.databaseBuilder(context,CartProductsDatabase::class.java,"CartProducts").allowMainThreadQueries().build()
                 INSTANCE = roomDb
                 return roomDb
             }
         }
     }
}