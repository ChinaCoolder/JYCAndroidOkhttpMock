package happy.jyc.mock

import android.content.Context
import happy.jyc.mock.construct.Constructor
import happy.jyc.mock.construct.ImageConstructor
import happy.jyc.mock.construct.JsonConstructor
import okhttp3.Request

internal class MockFactory private constructor() {
    companion object {
        private val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            MockFactory()
        }

        fun getInstance(): MockFactory =
            INSTANCE
    }

    fun getConstructor(
        context: Context,
        path: String,
        request: Request
    ): Constructor? {
        val jsonConstructor = JsonConstructor(context, path, request)
        if (jsonConstructor.acceptable()) {
            return jsonConstructor
        }
        val imageConstructor = ImageConstructor(context, path, request)
        if (imageConstructor.acceptable()) {
            return imageConstructor
        }
        return null
    }
}