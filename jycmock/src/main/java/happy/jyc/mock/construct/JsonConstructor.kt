package happy.jyc.mock.construct

import android.content.Context
import android.util.Log
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import happy.jyc.mock.JYCMockInterceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.File
import java.io.FileNotFoundException
import java.lang.Exception

class JsonConstructor(
    private val context: Context,
    private val path: String,
    private val request: Request
): Constructor() {

    companion object {
        private const val JSON_CONTENT = "content"
        private const val JSON_HEADER = "header"
        private const val MEDIA_JSON = "application/json"
        private const val JSON_FILTER = "filter"
        private const val JSON_WAIT = "wait"
        private const val JSON_ENABLE = "enable"
        private const val JSON_PARAM = "param"
        private const val JSON_CONFIG = "config"
    }

    private var json: JsonObject? = null

    override fun acceptable(): Boolean {
        json = null
        getJsonElement()
        return json != null
    }

    override fun construct(): Response {
        val waitTime = json!!.parseWait()
        if (waitTime != 0L) {
            try {
                Thread.sleep(waitTime)
            } catch (e: Exception) {
                Log.e(
                    JYCMockInterceptor.LOGGER,
                    "Wait $waitTime failed, message ${e.message}"
                )
            }
        }
        return Response.Builder().apply {
            code(200)
            request(request)
            protocol(Protocol.HTTP_1_1)
            val content = json!!.parseContent()
            message("OK")
            body(content.toResponseBody(MEDIA_JSON.toMediaTypeOrNull()))
            json!!.parseHeader().forEach {
                addHeader(it.first, it.second)
            }
        }.build()
    }

    private fun getJsonElement() {
        val jsonContent = getJsonContent()
        if (jsonContent.isNotEmpty()) {
            try {
                val jsonElement = JsonParser.parseString(jsonContent).let {
                    if (it.isJsonArray)
                        it.asJsonArray
                    else
                        it.asJsonObject
                }
                if (jsonElement is JsonObject) {
                    if (matchWithRequest(jsonElement.asJsonObject)) {
                        json = jsonElement
                    }
                } else {
                    jsonElement.asJsonArray.forEach {
                        if (matchWithRequest(it.asJsonObject)) {
                            json = it.asJsonObject
                            return@forEach
                        }
                    }
                }
            } catch (e: JsonParseException) {
                Log.e(
                    JYCMockInterceptor.LOGGER,
                    "Json parse error, please check is json format correct ${parsePath()}.json"
                )
            } catch (e: IllegalStateException) {
                Log.e(
                    JYCMockInterceptor.LOGGER,
                    "Json parse error, please check is json format correct ${parsePath()}.json"
                )
            }
        }
    }

    private fun matchWithRequest(
        jsonObject: JsonObject
    ): Boolean =
        jsonObject.parseEnable()
            && isJsonParamMatch(jsonObject)
            && isJsonHeaderMatch(jsonObject)

    private fun isJsonParamMatch(
        jsonObject: JsonObject
    ): Boolean =
        jsonObject.parseFilterParam().match(getRequestParams())

    private fun isJsonHeaderMatch(
        jsonObject: JsonObject
    ): Boolean =
        jsonObject.parseFilterHeader().match(getRequestHeader())

    private fun getJsonContent(): String {
        val filePath = "${path}${parsePath()}.json"
        var lines = ""
        try {
            context.assets.open(filePath).bufferedReader().use { reader ->
                lines = reader.readLines().let { list ->
                    var result = ""
                    list.forEach {
                        result += it
                    }
                    result
                }

            }
        } catch (e: FileNotFoundException) {
            Log.e(
                JYCMockInterceptor.LOGGER,
                "target assets file not found, will use default request"
            )
        }
        return lines
    }

    private fun getRequestHeader(): List<Pair<String, String>> =
        mutableListOf<Pair<String, String>>().apply {
            request.headers.iterator().let {
                while (it.hasNext()) {
                    this.add(it.next())
                }
            }
        }

    private fun getRequestParams(): List<Pair<String, String>> =
        mutableListOf<Pair<String, String>>().apply {
            request.url.queryParameterNames.forEach {
                this.add(
                    Pair(
                        it, request.url.queryParameter(it).orEmpty()
                    )
                )
            }
        }

    private fun JsonObject.parseHeader(): List<Pair<String, String>> =
        mutableListOf<Pair<String, String>>().apply {
            if (has(JSON_HEADER)) {
                val header = get(JSON_HEADER).asJsonObject
                header.keySet().forEach {
                    add(
                        Pair(
                            it,
                            header.get(it).asString
                        )
                    )
                }
            }
        }

    private fun JsonObject.parseContent(): String {
        var result = ""
        if (has(JSON_CONTENT)) {
            result = get(JSON_CONTENT).asJsonObject.toString()
        }
        return result
    }

    private fun JsonObject.parseFilterHeader(): List<Pair<String, String>> =
        mutableListOf<Pair<String, String>>().apply {
            val config = parseConfig()
            if (config != null && config.has(JSON_FILTER)) {
                val filter = config.get(JSON_FILTER).asJsonObject
                if (filter.has(JSON_HEADER)) {
                    val header = filter.get(JSON_HEADER).asJsonObject
                    header.keySet().forEach { headerKey ->
                        this.add(
                            Pair(
                                headerKey, header.get(headerKey).asString
                            )
                        )
                    }
                }
            }
        }

    private fun JsonObject.parseFilterParam(): List<Pair<String, String>> =
        mutableListOf<Pair<String, String>>().apply {
            val config = parseConfig()
            if (config != null && config.has(JSON_FILTER)) {
                val filter = config.get(JSON_FILTER).asJsonObject
                if (filter.has(JSON_PARAM)) {
                    val header = filter.get(JSON_PARAM).asJsonObject
                    header.keySet().forEach { headerKey ->
                        this.add(
                            Pair(
                                headerKey, header.get(headerKey).asString
                            )
                        )
                    }
                }
            }
        }

    private fun JsonObject.parseWait(): Long {
        val config = parseConfig()
        return if (config == null || !config.has(JSON_WAIT)) {
            0
        } else {
            config.get(JSON_WAIT).asNumber.toLong()
        }
    }

    private fun JsonObject.parseEnable(): Boolean {
        val config = parseConfig()
        return if (config == null || !config.has(JSON_ENABLE)) {
            true
        } else {
            config.get(JSON_ENABLE).asBoolean
        }
    }

    private fun JsonObject.parseConfig(): JsonObject? =
        if (has(JSON_CONFIG))
            get(JSON_CONFIG).asJsonObject
        else null

    private fun List<Pair<String, String>>.match(
        second: List<Pair<String, String>>
    ): Boolean {
        var count = 0
        val hash = mutableMapOf<String, String>()
        second.forEach {
            hash[it.first] = it.second
        }
        forEach {
            if (hash[it.first] != null && hash[it.first] == it.second) {
                count ++
            }
        }
        return count == this.size && count == second.size
    }

    private fun parsePath(): String =
        request.url.pathSegments.let { list ->
            var result = ""
            list.filter {
                it.trim().isNotEmpty()
            }.forEach {
                result += "${File.separator}${it}"
            }
            result
        }
}