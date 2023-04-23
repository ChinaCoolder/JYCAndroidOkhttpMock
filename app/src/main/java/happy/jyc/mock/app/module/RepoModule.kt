package happy.jyc.mock.app.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import happy.jyc.mock.app.net.UserService
import happy.jyc.mock.app.repo.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {
    @Singleton
    @Provides
    fun provideUserRepo(
        userService: UserService
    ): UserRepository = UserRepository(userService)
}