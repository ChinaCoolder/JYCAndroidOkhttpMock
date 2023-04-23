package happy.jyc.mock.app.module

import android.content.Context
import com.bumptech.glide.Glide
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import happy.jyc.mock.JYCMockInterceptor
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GlobalModule {

    @Singleton
    @Provides
    fun providePicasso(
        @ApplicationContext context: Context,
        client: OkHttpClient
    ): Picasso =
        Picasso.Builder(context)
            .downloader(
                OkHttp3Downloader(client)
            ).build()

    @Singleton
    @Provides
    fun provideOkhttpClient(
        @ApplicationContext context: Context
    ): OkHttpClient =
        OkHttpClient.Builder()
            .callTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(JYCMockInterceptor(context))
            .build()

    @Singleton
    @Provides
    fun provideRetrofit(
        client: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://192.168.1.105:8080/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}