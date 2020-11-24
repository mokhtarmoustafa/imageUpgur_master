package dev.mokhtar0.upgur.ui.login

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import dev.mokhtar0.upgur.Injector
import dev.mokhtar0.upgur.data.config.Credentials
import dev.mokhtar0.upgur.data.model.AuthResult
import dev.mokhtar0.upgur.data.remote.*
import javax.inject.Inject

class LoginAuthResponseViewModel : ViewModel() {
    @Inject
    lateinit var client: ImgurClient
    @Inject
    lateinit var credentials: Credentials

    val authResult = MediatorLiveData<Resource<AuthResult>>().also { Resource.loading(null) }

    fun doAuth() {
        Injector.get().inject(this)
        val response = client.oauth2Token(credentials.refreshToken)
        authResult.addSource(response) {
            when (it) {
                is ApiSuccessResponse<*> -> {
                    authResult.value = Resource.success((it as ApiSuccessResponse).body)
                }
                is ApiEmptyResponse<*> -> {
                    authResult.value = Resource.empty()
                }
                is ApiErrorResponse<*> -> {
                    authResult.value = Resource.error(
                        (it as ApiErrorResponse).errorMessage,
                        null
                    )
                }
            }
        }
    }
}