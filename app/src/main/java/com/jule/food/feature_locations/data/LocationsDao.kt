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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addLocations(locations: List<GroceryLocationNew>)

    @Query("DELETE FROM GroceryLocationNew WHERE id IN (:locationIds)")
    suspend fun deleteLocations(locationIds: List<Int>)
}