package com.weathersnap.di

import android.content.Context
import androidx.room.Room
import com.weathersnap.data.local.AppDatabase
import com.weathersnap.data.local.dao.ReportDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "weathersnap_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideReportDao(database: AppDatabase): ReportDao {
        return database.reportDao()
    }
}
