package io.github.plastix.buzz.thread

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Qualifier
annotation class IO

@Module
@InstallIn(SingletonComponent::class)
object ThreadModule {

    @IO
    @Singleton
    @Provides
    fun provideIoContext(): CoroutineContext = Dispatchers.IO
}