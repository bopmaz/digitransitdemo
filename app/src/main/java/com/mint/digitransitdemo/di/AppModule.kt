package com.mint.digitransitdemo.di

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.mint.digitransitdemo.data.StopRepositoryImpl
import com.mint.digitransitdemo.data.AuthorizationInterceptor
import com.mint.digitransitdemo.domain.GetStopUseCase
import com.mint.digitransitdemo.domain.GetStopsUseCase
import com.mint.digitransitdemo.domain.StopRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideInterceptor(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val authorizationInterceptor = AuthorizationInterceptor()
        return OkHttpClient
            .Builder()
            .addInterceptor(interceptor)
            .addInterceptor(authorizationInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideApolloClient(okHttpClient: OkHttpClient): ApolloClient {
        return ApolloClient
            .Builder()
            .serverUrl("https://api.digitransit.fi/routing/v1/routers/finland/index/graphql")
            .okHttpClient(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideStopClient(apolloClient: ApolloClient): StopRepository {
        return StopRepositoryImpl(apolloClient)
    }

    @Provides
    @Singleton
    fun provideStopUseCase(stopRepository: StopRepository): GetStopUseCase {
        return GetStopUseCase(stopRepository)
    }

    @Provides
    @Singleton
    fun provideStopsUseCase(stopRepository: StopRepository): GetStopsUseCase {
        return GetStopsUseCase(stopRepository)
    }
}