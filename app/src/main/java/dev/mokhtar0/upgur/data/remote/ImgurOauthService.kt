package dev.mokhtar0.upgur.data.remote

import androidx.lifecycle.LiveData
import dev.mokhtar0.upgur.data.model.*
import retrofit2.http.*

interface ImgurOauthService {
    @POST("oauth2/token")
    @FormUrlEncoded
    fun oauth2Token(
        @Field("refresh_token") refreshToken: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("grant_type") grantType: String
    ): LiveData<ApiResponse<AuthResult>>
}