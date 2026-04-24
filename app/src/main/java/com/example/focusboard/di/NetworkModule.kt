package com.example.focusboard.di

import com.example.focusboard.BuildConfig // Phải có dòng này để lấy cấu hình từ Gradle
import com.example.focusboard.data.remote.api.FocusBoardApiService
import com.example.focusboard.data.remote.auth.AuthHeaderInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // KHÔNG dùng "private const val" ở đây nữa để tránh KSP bị lỗi nhịp.

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authHeaderInterceptor: AuthHeaderInterceptor,
    ): OkHttpClient {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Dùng Logcat filter tag:OkHttp như bạn nói
        }
        return OkHttpClient.Builder()
            .addInterceptor(authHeaderInterceptor)
            .addInterceptor(logger)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            // Truyền trực tiếp BuildConfig.BASE_URL vào đây.
            // Lúc này file BuildConfig đã được Gradle tạo ra xong, KSP không bị lỗi nữa,
            // và tính năng tự động đổi URL Debug/Release của bạn vẫn hoạt động 100%.
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideFocusBoardApi(retrofit: Retrofit): FocusBoardApiService =
        retrofit.create(FocusBoardApiService::class.java)
}