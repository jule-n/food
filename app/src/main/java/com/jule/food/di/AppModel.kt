package com.jule.food.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.jule.food.feature_groceries.data.GroceriesRepository
import com.jule.food.feature_groceries.data.source.GroceriesDatabase
import com.jule.food.feature_groceries.domain.use_case.AddGroceryItem
import com.jule.food.feature_groceries.domain.use_case.AddGroceryItems
import com.jule.food.feature_groceries.domain.use_case.AddGroceryList
import com.jule.food.feature_groceries.domain.use_case.AddGroceryLists
import com.jule.food.feature_groceries.domain.use_case.DeleteGroceryItems
import com.jule.food.feature_groceries.domain.use_case.DeleteGroceryLists
import com.jule.food.feature_groceries.domain.use_case.GetAllLists
import com.jule.food.feature_groceries.domain.use_case.GetGroceriesInList
import com.jule.food.feature_groceries.domain.use_case.GroceriesUseCases
import com.jule.food.feature_groceries.domain.use_case.RemoveRecipeIdFromGroceries
import com.jule.food.feature_locations.data.LocationsRepository
import com.jule.food.feature_locations.data.source.LocationsDatabase
import com.jule.food.feature_locations.domain.use_case.AddItemNameToLocation
import com.jule.food.feature_locations.domain.use_case.AddItemNamesToLocation
import com.jule.food.feature_locations.domain.use_case.AddLocations
import com.jule.food.feature_locations.domain.use_case.DeleteLocations
import com.jule.food.feature_locations.domain.use_case.GetAllLocations
import com.jule.food.feature_locations.domain.use_case.LocationUseCases
import com.jule.food.others.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGroceriesDatabase(app: Application): GroceriesDatabase {
        return Room.databaseBuilder<GroceriesDatabase>(
            app.applicationContext, GroceriesDatabase::class.java, "groceries"
        ).build()
    }

    @Provides
    @Singleton
    fun provideGroceriesRepository(db: GroceriesDatabase): GroceriesRepository {
        return GroceriesRepository(db.groceriesDao())
    }

    @Provides
    @Singleton
    fun provideGroceriesUseCases(repository: GroceriesRepository): GroceriesUseCases {
        return GroceriesUseCases(
            addGroceryItem = AddGroceryItem(repository),
            addGroceryList = AddGroceryList(repository),
            addGroceryLists = AddGroceryLists(repository),
            addGroceryItems = AddGroceryItems(repository),
            deleteGroceryItems = DeleteGroceryItems(repository),
            deleteGroceryLists = DeleteGroceryLists(repository),
            getGroceriesInList = GetGroceriesInList(repository),
            getAllLists = GetAllLists(repository),
            removeRecipeIdFromGroceries = RemoveRecipeIdFromGroceries(repository)
        )
    }


    @Provides
    @Singleton
    fun provideLocationsDatabase(app: Application): LocationsDatabase {
        return Room.databaseBuilder<LocationsDatabase>(
            app.applicationContext, LocationsDatabase::class.java, "locations"
        ).build()
    }

    @Provides
    @Singleton
    fun provideLocationsRepository(db: LocationsDatabase): LocationsRepository {
        return LocationsRepository(db.locationsDao())
    }

    @Provides
    @Singleton
    fun provideLocationsUseCases(repository: LocationsRepository): LocationUseCases {
        return LocationUseCases(
            addLocations = AddLocations(repository),
            deleteLocations = DeleteLocations(repository),
            getAllLocations = GetAllLocations(repository),
            addItemNameToLocation = AddItemNameToLocation(repository),
            addItemNamesToLocation = AddItemNamesToLocation(repository),
        )
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(@ApplicationContext context: Context): SettingsRepository {
        return SettingsRepository(context)
    }
}