package happy.jyc.mock.construct

import android.content.Context
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.File

class ImageConstructor(
    private val context: Context,
    private val path: String,
    private val request: Request
) : Constructor() {
    companion object {
        private const val EXT_JPG = ".jpg"
        private const val EXT_JPEG = ".jpeg"
        private const val EXT_PNG = ".png"
        private const val EXT_WEBP = ".webp"
    }

    private var imageFilePath = ""

    override fun acceptable(): Boolean {
        imageFilePath = findImage()
        return imageFilePath.isNotEmpty()
    }

    override fun construct(): Response =
        Response.Builder().apply {
            code(200)
            request(request)
            protocol(Protocol.HTTP_1_1)
            val mediaType = "image/${
                when {
                    imageFilePath.endsWith(EXT_WEBP) -> "webp"
                    imageFilePath.endsWith(EXT_PNG) -> "png"
                    else -> "jpeg"
                }
            }"
            val content: ByteArray
            context.assets.open(imageFilePath).use {
                content = it.readBytes()
            }
            body(
                content.toResponseBody(
                    mediaType.toMediaTypeOrNull()
                )
            )
            message("OK")
        }.build()

    private fun findImage(): String {
        var imagePath = ""
        val assetsPath: String = path + File.separator + request.url.pathSegments.dropLast(1).let { list ->
            var result = ""
            list.forEach {
                result += it + File.separator
            }
            result
        }
        context.assets.list(assetsPath)?.forEach {
            val name = request.url.pathSegments.last()
            when(it) {
                "${name}${EXT_JPEG}" -> {
                    imagePath = "${assetsPath}${name}${EXT_JPEG}"
                    return@forEach
                }
                "${name}${EXT_JPG}" -> {
                    imagePath = "${assetsPath}${name}${EXT_JPG}"
                    return@forEach
                }
                "${name}${EXT_PNG}" -> {
                    imagePath = "${assetsPath}${name}${EXT_PNG}"
                    return@forEach
                }
                "${name}${EXT_WEBP}" -> {
                    imagePath = "${assetsPath}${name}${EXT_WEBP}"
                    return@forEach
                }
            }
        }
        return imagePath
    }
}