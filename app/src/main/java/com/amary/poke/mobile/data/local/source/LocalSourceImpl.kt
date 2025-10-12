package com.amary.poke.mobile.data.local.source

import com.amary.poke.mobile.data.local.dto.AuthDto
import com.amary.poke.mobile.data.local.dto.ResultDto
import com.amary.poke.mobile.data.local.dto.UserDto
import com.couchbase.lite.Collection
import com.couchbase.lite.CouchbaseLiteException
import com.couchbase.lite.DataSource
import com.couchbase.lite.Expression
import com.couchbase.lite.FullTextIndexItem
import com.couchbase.lite.IndexBuilder
import com.couchbase.lite.Meta
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.Ordering
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import com.couchbase.lite.UnitOfWork
import com.couchbase.lite.ValueIndexItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.UUID

class LocalSourceImpl(
    private val authCollection: Collection,
    private val userCollection: Collection,
    private val pokeCollection: Collection,
    private val ioDispatcher: CoroutineDispatcher
): LocalSource {

    init {
        createIndexes()
    }

    private fun createIndexes() {
        try {
            pokeCollection.createIndex(
                "typeIndex",
                IndexBuilder.valueIndex(ValueIndexItem.property(TYPE_FIELD))
            )
            pokeCollection.createIndex(
                "nameIndex",
                IndexBuilder.valueIndex(ValueIndexItem.property(NAME_FIELD))
            )
            pokeCollection.createIndex(
                "timestampIndex",
                IndexBuilder.valueIndex(ValueIndexItem.property(TIMESTAMP_FIELD))
            )
            pokeCollection.createIndex(
                "ftsNameIndex",
                IndexBuilder.fullTextIndex(FullTextIndexItem.property(NAME_FIELD))
            )

            userCollection.createIndex(
                "userNameIndex",
                IndexBuilder.valueIndex(ValueIndexItem.property("user_name"))
            )
            userCollection.createIndex(
                "userIdIndex",
                IndexBuilder.valueIndex(ValueIndexItem.property("id"))
            )
            userCollection.createIndex(
                "loginIndex",
                IndexBuilder.valueIndex(
                    ValueIndexItem.property("user_name"),
                    ValueIndexItem.property("password")
                )
            )

            authCollection.createIndex(
                "authIdIndex",
                IndexBuilder.valueIndex(ValueIndexItem.property("id"))
            )

        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
    }

    override suspend fun getAll(): List<ResultDto> = withContext(ioDispatcher) {
        try {
            val query = QueryBuilder
                .select(SelectResult.all())
                .from(DataSource.collection(pokeCollection))
                .where(Expression.property(TYPE_FIELD).equalTo(Expression.string(DOC_TYPE)))
                .orderBy(Ordering.property(NAME_FIELD).ascending())

            val resultSet = query.execute()
            val results = mutableListOf<ResultDto>()

            resultSet.allResults().forEach { result ->
                val dict = result.getDictionary(pokeCollection.name)
                dict?.let {
                    results.add(
                        ResultDto(
                            name = it.getString(NAME_FIELD).orEmpty(),
                            url = it.getString(URL_FIELD).orEmpty()
                        )
                    )
                }
            }

            results
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun deleteAll() = withContext(ioDispatcher) {
        try {
            val query = QueryBuilder
                .select(SelectResult.expression(Meta.id))
                .from(DataSource.collection(pokeCollection))
                .where(Expression.property(TYPE_FIELD).equalTo(Expression.string(DOC_TYPE)))

            val resultSet = query.execute()

            pokeCollection.database.inBatch(UnitOfWork {
                resultSet.allResults().forEach { result ->
                    val docId = result.getString(0)
                    docId?.let {
                        val doc = pokeCollection.getDocument(it)
                        doc?.let { document -> pokeCollection.delete(document) }
                    }
                }
            })
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun insert(results: List<ResultDto>) = withContext(ioDispatcher) {
        try {
            pokeCollection.database.inBatch(UnitOfWork {
                results.forEach { result ->
                    val docId = generateDocId(result.name)
                    val doc = MutableDocument(docId).apply {
                        setString(TYPE_FIELD, DOC_TYPE)
                        setString(NAME_FIELD, result.name)
                        setString(URL_FIELD, result.url)
                        setLong(TIMESTAMP_FIELD, System.currentTimeMillis())
                    }
                    pokeCollection.save(doc)
                }
            })
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun isUsernameExists(username: String): Boolean = withContext(ioDispatcher) {
        try {
            val query = QueryBuilder
                .select(SelectResult.expression(Expression.property("user_name")))
                .from(DataSource.collection(userCollection))
                .where(Expression.property("user_name").equalTo(Expression.string(username)))
                .limit(Expression.intValue(1))

            val resultSet = query.execute()
            return@withContext resultSet.allResults().isNotEmpty()
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }

    override suspend fun insertUser(user: UserDto) = withContext(ioDispatcher) {
        try {
            val userId = UUID.randomUUID().toString()
            val newUser = user.copy(id = userId)
            val document = MutableDocument(userId).apply {
                setData(newUser.toMap())
            }
            userCollection.save(document)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun getUserById(userId: String): UserDto? = withContext(ioDispatcher) {
        try {
            val query = QueryBuilder
                .select(SelectResult.all())
                .from(DataSource.collection(userCollection))
                .where(Expression.property("id").equalTo(Expression.string(userId)))
                .limit(Expression.intValue(1))

            val resultSet = query.execute()
            val result = resultSet.allResults().firstOrNull()

            val dict = result?.getDictionary(userCollection.name)
            val userMap = dict?.toMap()

            return@withContext UserDto.fromMap(userMap)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    override suspend fun login(
        username: String,
        password: String
    ): UserDto? = withContext(ioDispatcher) {
        try {
            val query = QueryBuilder
                .select(SelectResult.all())
                .from(DataSource.collection(userCollection))
                .where(
                    Expression.property("user_name").equalTo(Expression.string(username))
                        .and(Expression.property("password").equalTo(Expression.string(password)))
                )
                .limit(Expression.intValue(1))

            val resultSet = query.execute()
            val result = resultSet.allResults().firstOrNull()

            val dict = result?.getDictionary(userCollection.name)
            val userMap = dict?.toMap()

            return@withContext UserDto.fromMap(userMap)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    override suspend fun insertAuth(auth: AuthDto) = withContext(ioDispatcher) {
        try {
            val document = MutableDocument(auth.id).apply {
                setData(auth.toMap())
            }
            authCollection.save(document)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun logout() = withContext(ioDispatcher) {
        try {
            val query = QueryBuilder
                .select(SelectResult.expression(Meta.id))
                .from(DataSource.collection(authCollection))

            val resultSet = query.execute()

            authCollection.database.inBatch(UnitOfWork {
                resultSet.allResults().forEach { result ->
                    val docId = result.getString(0)
                    docId?.let {
                        val document = authCollection.getDocument(it)
                        document?.let { doc -> authCollection.delete(doc) }
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun isAuthenticated(): Boolean = withContext(ioDispatcher) {
        try {
            val query = QueryBuilder
                .select(SelectResult.expression(Meta.id))
                .from(DataSource.collection(authCollection))
                .limit(Expression.intValue(1))

            val resultSet = query.execute()
            val isAuth = resultSet.allResults().isNotEmpty()

            return@withContext isAuth
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }

    override suspend fun getAuth(): AuthDto? = withContext(ioDispatcher) {
        try {
            val query = QueryBuilder
                .select(SelectResult.all())
                .from(DataSource.collection(authCollection))
                .limit(Expression.intValue(1))

            val resultSet = query.execute()
            val result = resultSet.allResults().firstOrNull()

            if (result == null) {
                return@withContext null
            }

            val dict = result.getDictionary(authCollection.name)
            val authMap = dict?.toMap()

            return@withContext AuthDto.fromMap(authMap)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    private fun generateDocId(name: String): String {
        return "pokemon_${name.lowercase().replace(" ", "_")}"
    }

    private companion object {
        private const val DOC_TYPE = "pokemon"
        private const val TYPE_FIELD = "type"
        private const val NAME_FIELD = "name"
        private const val URL_FIELD = "url"
        private const val TIMESTAMP_FIELD = "timestamp"
    }
}