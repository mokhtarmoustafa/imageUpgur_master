package dev.mokhtar0.upgur

import android.content.Context
import androidx.room.Room
import dagger.Component
import dagger.Module
import dagger.Provides
import dev.mokhtar0.upgur.data.db.AlbumDao
import dev.mokhtar0.upgur.data.db.AppDatabase
import dev.mokhtar0.upgur.data.db.ImageDao
import dev.mokhtar0.upgur.data.remote.upload.ImageUploadWorker
import dev.mokhtar0.upgur.ui.album.AlbumDetailsViewModel
import dev.mokhtar0.upgur.ui.albums.AlbumsViewModel
import dev.mokhtar0.upgur.ui.login.LoginAuthResponseActivity
import dev.mokhtar0.upgur.ui.login.LoginAuthResponseViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [ContextModule::class, DatabaseModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
    fun inject(activity: LoginAuthResponseActivity)
    fun inject(activity: LoginAuthResponseViewModel)
    fun inject(activity: AlbumsViewModel)
    fun inject(activity: AlbumDetailsViewModel)
    fun inject(activity: ImageUploadWorker)
}

@Module
class ContextModule(private val appContext: Context) {
    @Provides
    fun appContext(): Context = appContext
}

@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideDb(appContext: Context): AppDatabase {
        return Room
            .databaseBuilder(appContext, AppDatabase::class.java, "upgur.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideAlbumDao(db: AppDatabase): AlbumDao {
        return db.albumDao()
    }

    @Singleton
    @Provides
    fun provideImageDao(db: AppDatabase): ImageDao {
        return db.imageDao()
    }
}

class Injector private constructor() {
    companion object {
        fun get() = App.get().component
    }
}