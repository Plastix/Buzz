package io.github.plastix.buzz.persistence

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Database(
    entities = [
        PuzzleEntity::class,
        PuzzleGameStateEntity::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class PuzzleDatabase : RoomDatabase() {
    abstract fun puzzleDao(): PuzzleDao
}


@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(app: Application): PuzzleDatabase {
        return Room.databaseBuilder(app, PuzzleDatabase::class.java, "buzz-database")
            .fallbackToDestructiveMigration() // TODO good enough for now
            .build()
    }
}