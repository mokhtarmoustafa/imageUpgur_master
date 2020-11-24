package dev.mokhtar0.upgur.ui.albums

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.mokhtar0.upgur.AppExecutors
import dev.mokhtar0.upgur.data.config.Credentials
import dev.mokhtar0.upgur.data.db.AlbumDao
import dev.mokhtar0.upgur.data.model.AccountAvatar
import dev.mokhtar0.upgur.data.model.Album
import dev.mokhtar0.upgur.data.remote.ImgurClient
import dev.mokhtar0.upgur.data.remote.NetworkBoundResource
import dev.mokhtar0.upgur.data.remote.Resource
import dev.mokhtar0.upgur.util.RateLimiter
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlbumsRepository @Inject constructor(
    private val executors: AppExecutors,
    private val albumDao: AlbumDao,
    private val client: ImgurClient,
    private val credentials: Credentials
) {
    private val rateLimiter = RateLimiter<String>(30, TimeUnit.SECONDS)

    private val rateLimiterKey = "albums"
    private val rateLimiterAvatarKey = "avatar"

    fun loadAvatar(): LiveData<Resource<AccountAvatar>> {
        return object : NetworkBoundResource<AccountAvatar, AccountAvatar>(executors) {
            override fun saveCallResult(result: AccountAvatar) {
                credentials.avatarUrl = result.avatar
            }

            override fun shouldFetch(data: AccountAvatar?): Boolean {
                return data == null || rateLimiter.shouldFetch(rateLimiterAvatarKey)
            }

            override fun loadFromDb() = MutableLiveData<AccountAvatar>().also {
                it.value = AccountAvatar(credentials.avatarUrl ?: "")
            }

            override fun createCall() = client.accountAvatar()

            override fun onFetchFailed() {
                rateLimiter.reset(rateLimiterAvatarKey)
            }
        }.asLiveData()
    }

    fun loadAlbums(): LiveData<Resource<List<Album>>> {
        return object : NetworkBoundResource<List<Album>, List<Album>>(executors) {
            override fun saveCallResult(items: List<Album>) {
                albumDao.insertAll(items)
            }

            override fun shouldFetch(data: List<Album>?): Boolean {
                return data == null || data.isEmpty() || rateLimiter.shouldFetch(rateLimiterKey)
            }

            override fun loadFromDb() = albumDao.getAll()

            override fun createCall() = client.albums()

            override fun onFetchFailed() {
                rateLimiter.reset(rateLimiterKey)
            }
        }.asLiveData()
    }

    fun resetRatelimit() {
        rateLimiter.reset(rateLimiterKey)
    }
}