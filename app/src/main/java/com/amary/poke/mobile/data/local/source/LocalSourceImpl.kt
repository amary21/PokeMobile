package com.amary.poke.mobile.data.local.source

import com.amary.poke.mobile.data.local.dto.AuthDto
import com.amary.poke.mobile.data.local.dto.UserDto
import com.couchbase.lite.Collection
import com.couchbase.lite.DataSource
import com.couchbase.lite.Expression
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult

class LocalSourceImpl(
    private val authCollection: Collection,
    private val userCollection: Collection,
): LocalSource {
    override suspend fun isUsernameExists(username: String): Boolean {
        val query = QueryBuilder
            .select(SelectResult.expression(Expression.property("user_name")))
            .from(DataSource.collection(userCollection))
            .where(Expression.property("user_name").equalTo(Expression.string(username)))
            .limit(Expression.intValue(1))

        val resultSet = query.execute()
        return resultSet.allResults().isNotEmpty()
    }

    override suspend fun insertUser(user: UserDto) {
        val document = MutableDocument(
            user.id.toString(),
            user.toMap()
        )

        userCollection.save(document)
    }

    override suspend fun getUserById(userId: Int): UserDto? {
        val query = QueryBuilder
            .select(SelectResult.all())
            .from(DataSource.collection(userCollection))
            .where(Expression.property("id").equalTo(Expression.intValue(userId)))
            .limit(Expression.intValue(1))

        val resultSet = query.execute()
        val result = resultSet.allResults().firstOrNull() ?: return null
        val userMap = result.toMap()
        return UserDto.fromMap(userMap)
    }

    override suspend fun login(
        username: String,
        password: String
    ): UserDto? {
        val query = QueryBuilder
            .select(SelectResult.all())
            .from(DataSource.collection(userCollection))
            .where(
                Expression.property("user_name").equalTo(Expression.string(username))
                    .and(Expression.property("password").equalTo(Expression.string(password)))
            )
            .limit(Expression.intValue(1))

        val resultSet = query.execute()
        val result = resultSet.allResults().firstOrNull() ?: return null
        val userMap = result.toMap()
        return UserDto.fromMap(userMap)
    }

    override suspend fun insertAuth(auth: AuthDto) {
        val document = MutableDocument(
            auth.id.toString(),
            auth.toMap()
        )

        authCollection.save(document)
    }

    override suspend fun logout() {
        val query = QueryBuilder
            .select(SelectResult.all())
            .from(DataSource.collection(authCollection))

        val resultSet = query.execute()
        val results = resultSet.allResults()
        for (result in results) {
            val authId = result.getString("id") ?: continue
            val document = authCollection.getDocument(authId) ?: continue
            authCollection.delete(document)
        }
    }

    override suspend fun isAuthenticated(): Boolean {
        val query = QueryBuilder
            .select(SelectResult.expression(Expression.property("id")))
            .from(DataSource.collection(authCollection))
            .limit(Expression.intValue(1))

        val resultSet = query.execute()
        return resultSet.allResults().isNotEmpty()
    }

    override suspend fun getAuth(): AuthDto? {
        val query = QueryBuilder
            .select(SelectResult.all())
            .from(DataSource.collection(authCollection))
            .limit(Expression.intValue(1))

        val resultSet = query.execute()
        val result = resultSet.allResults().firstOrNull() ?: return null
        val authMap = result.toMap()
        return AuthDto.fromMap(authMap)
    }
}