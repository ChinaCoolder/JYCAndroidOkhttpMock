package happy.jyc.mock.app

import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory
import com.facebook.imagepipeline.core.ImagePipelineConfig
import dagger.hilt.android.HiltAndroidApp
import happy.jyc.mock.JYCMockInterceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class Application: android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this, OkHttpImagePipelineConfigFactory.newBuilder(this,
            OkHttpClient.Builder()
                .callTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .addInterceptor(JYCMockInterceptor(this))
                .build()
            ).build())
    }
}