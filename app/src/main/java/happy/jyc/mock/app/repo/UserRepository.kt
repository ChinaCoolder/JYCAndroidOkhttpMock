package happy.jyc.mock.app.repo

import happy.jyc.mock.app.net.User
import happy.jyc.mock.app.net.UserService
import happy.jyc.mock.app.net.UserWithHeader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

class UserRepository @Inject constructor(
    private val userService: UserService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    fun getUser(id: Long): Flow<User> = flow {
        emit(userService.getUser(id))
    }.flowOn(dispatcher)

    fun getUserByParam(param: String): Flow<User> = flow {
        emit(userService.getUserWithParam(param))
    }.flowOn(dispatcher)

    fun getUserByHeader(header: String): Flow<User> = flow {
        emit(userService.getUserWithHeader(header))
    }

    fun getUserWithHeader(id: Long): Flow<UserWithHeader> = flow {
        val response = userService.getUserCall(id).execute()
        emit(
            UserWithHeader(
                response.body()!!,
                mutableListOf<Pair<String, String>>().apply {
                    response.headers().iterator().let { iterator ->
                        while (iterator.hasNext()){
                            add(iterator.next())
                        }
                    }
                }
            )
        )
    }.flowOn(dispatcher)
}