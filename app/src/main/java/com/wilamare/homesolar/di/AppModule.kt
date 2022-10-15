package com.wilamare.homesolar.di

import com.wilamare.homesolar.data.remote.MqttService
import com.wilamare.homesolar.data.remote.MqttServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun providesMqttService(): MqttService {
        return MqttServiceImpl()
    }
}