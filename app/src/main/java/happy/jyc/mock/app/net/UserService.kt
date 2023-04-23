package happy.jyc.mock.app.net

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {
    @GET("user/{userId}")
    suspend fun getUser(@Path("userId") id: Long): User

    @GET("user/{userId}")
    fun getUserCall(@Path("userId") id: Long): Call<User>

    @GET("user")
    suspend fun getUserWithParam(@Query("test") test: String): User

    @GET("user")
    suspend fun getUserWithHeader(@Header("test") test: String): User
}