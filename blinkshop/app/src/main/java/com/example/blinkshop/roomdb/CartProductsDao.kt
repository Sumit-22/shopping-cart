package com.example.blinkshop.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CartProductsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCartProduct(products: CartProducts)
    @Update
    fun updateCartProduct(products: CartProducts)

    @Query("SELECT * FROM CartProductsTable") //IT SHOULD BE HERE TO SEE DATA IN TABLE
    fun getAllCartProducts() : Flow<List<CartProducts>>

    @Query("DELETE FROM CartProductsTable WHERE productId= :productId")  //imp thing why we use colon
    fun deleteCartProduct(productId : String)

    @Query("DELETE FROM CartProductsTable")
    fun deleteCartProducts()
}