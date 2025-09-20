package com.example.miam.di
import android.content.Context
import com.example.miam.data.AppDatabase
import com.example.miam.repository.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module @InstallIn(SingletonComponent::class)
object AppModule {
  @Provides @Singleton fun provideDb(@ApplicationContext ctx: Context) = AppDatabase.get(ctx)
  @Provides @Singleton fun provideRepository(db: AppDatabase) = Repository(db)
}
