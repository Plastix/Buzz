package io.github.plastix.buzz.persistence.gen

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Database(
    entities = [
        DictionaryWordEntity::class,
        PuzzleSeedEntity::class
    ],
    version = 2
)
abstract class DictionaryDatabase : RoomDatabase() {
    abstract fun dictionaryDao(): DictionaryDao
}


@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(app: Application): DictionaryDatabase {
        return Room.databaseBuilder(app, DictionaryDatabase::class.java, "buzz-dictionary-database")
            .createFromAsset("database/dictionary.db")
            // Allows us to bump the dictionary database schema version to have the app repopulate
            // the database wit new words.
            .fallbackToDestructiveMigration()
            .build()
    }
}