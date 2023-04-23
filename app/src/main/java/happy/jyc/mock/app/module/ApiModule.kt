package happy.jyc.mock.app.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import happy.jyc.mock.app.net.UserService
import retrofit2.Retrofit
import retrofit2.http.Streaming
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Singleton
    @Provides
    fun provideUserService(
        retrofit: Retrofit
    ): UserService =
        retrofit.create(UserService::class.java)
}