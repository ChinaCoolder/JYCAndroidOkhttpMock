package happy.jyc.mock

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.lang.Exception

class JYCMockInterceptor(
    private val context: Context,
    private val path: String = "jycmock",
    private val needMock: (Context, Request) -> Boolean = { _,_ ->
        BuildConfig.DEBUG
    }
): Interceptor {
    companion object {
        internal const val LOGGER = "JYCMockInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        if (needMock(context, chain.request())) {
            try {
                val result = parseResponse(
                    chain.request()
                )
                if (result != null) {
                    return result
                } else {
                    Log.e(
                        LOGGER,
                        "parse failed for request ${chain.request().url.toUrl()}"
                    )
                }
            } catch (e: Exception) {
                Log.e(
                    LOGGER,
                    "Unknown Mock Error, ${e.message}"
                )
            }
        }
        return chain.proceed(chain.request())
    }

    private fun parseResponse(
        request: Request
    ): Response? =
        MockFactory.getInstance().getConstructor(context, path, request)?.construct()
}