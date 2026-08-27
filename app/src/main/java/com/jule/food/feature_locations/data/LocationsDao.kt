package com.jule.food.feature_locations.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jule.food.feature_locations.domain.GroceryLocationNew
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationsDao {
    @Query("SELECT * FROM GroceryLocationNew")
    fun getAllLocations(): Flow<List<GroceryLocationNew>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addLocation(location: GroceryLocationNew)

    @Delete
    suspend fun deleteLocation(location: GroceryLocationNew)
}