package com.aikonia.app.data.source.local

import com.aikonia.app.data.source.local.User
import com.aikonia.app.data.source.local.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepositoryImpl(private val userDao: UserDao) : UserRepository {
    override suspend fun saveUser(user: User) {
        withContext(Dispatchers.IO) {
            userDao.insertUser(user)
        }
    }

    override suspend fun getUserById(userId: Int): User? {
        return withContext(Dispatchers.IO) {
            userDao.getUserById(userId)
        }
    }
}