package com.example.runnerapp.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.example.runnerapp.data.room.RunDao
import com.example.runnerapp.data.room.RunDatabase
import com.example.runnerapp.utils.Constants.KEY_NAME
import com.example.runnerapp.utils.Constants.KEY_USERS_DATA_GIVEN
import com.example.runnerapp.utils.Constants.KEY_WEIGHT
import com.example.runnerapp.utils.Constants.RUN_DATABASE_NAME
import com.example.runnerapp.utils.Constants.SHARED_PREFERENCES_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideRunDatabase(@ApplicationContext app: Context): RunDatabase {
        return Room.databaseBuilder(app, RunDatabase::class.java, RUN_DATABASE_NAME).build()
    }

    @Singleton
    @Provides
    fun provideRunDao(db: RunDatabase): RunDao {
        return db.getDao()
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app: Context): SharedPreferences {
        return app.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideUsersName(sharedPrefs: SharedPreferences) = sharedPrefs.getString(KEY_NAME, "") ?: ""

    @Singleton
    @Provides
    fun provideUsersWeight(sharedPrefs: SharedPreferences) = sharedPrefs.getFloat(KEY_WEIGHT, 60f)

    @Singleton
    @Provides
    fun provideIfUserEnteredData(sharedPrefs: SharedPreferences) =
        sharedPrefs.getBoolean(
            KEY_USERS_DATA_GIVEN, false
        )
}