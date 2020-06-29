package com.example.runnerapp.di

import android.content.Context
import androidx.room.Room
import com.example.runnerapp.data.room.RunDao
import com.example.runnerapp.data.room.RunDatabase
import com.example.runnerapp.utils.Constants.RUN_DATABASE_NAME
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
}