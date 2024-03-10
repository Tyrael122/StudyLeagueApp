package br.studyleague.data.repositories

import br.studyleague.BuildConfig
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import util.serializers.LocalDateTimeSerializer
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

object RetrofitBuilder {
    private const val BASE_URL = BuildConfig.API_URL

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        serializersModule = SerializersModule {
            contextual(LocalDateTime::class, LocalDateTimeSerializer())
        }
    }

    private const val defaultTimeout = 60L

    private var client =
        OkHttpClient().newBuilder().connectTimeout(defaultTimeout, TimeUnit.SECONDS)
            .writeTimeout(defaultTimeout, TimeUnit.SECONDS)
            .readTimeout(defaultTimeout, TimeUnit.SECONDS).build()

    fun buildRetrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .baseUrl(BASE_URL).client(client).build()
    }
}