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
        val query = QueryBuilder
            .select(SelectResult.expression(Expression.property("user_name")))
            .from(DataSource.collection(userCollection))
            .where(Expression.property("user_name").equalTo(Expression.string(username)))
            .limit(Expression.intValue(1))

        val resultSet = query.execute()
        resultSet.allResults().isNotEmpty()
    }

    override suspend fun insertUser(user: UserDto) = withContext(ioDispatcher) {
        val document = MutableDocument(
            user.id.toString(),
            user.toMap()
        )

        userCollection.save(document)
    }

    override suspend fun getUserById(userId: Int): UserDto? = withContext(ioDispatcher) {
        val query = QueryBuilder
            .select(SelectResult.all())
            .from(DataSource.collection(userCollection))
            .where(Expression.property("id").equalTo(Expression.intValue(userId)))
            .limit(Expression.intValue(1))

        val resultSet = query.execute()
        val result = resultSet.allResults().firstOrNull()
        val userMap = result?.toMap()
        UserDto.fromMap(userMap)
    }

    override suspend fun login(
        username: String,
        password: String
    ): UserDto? = withContext(ioDispatcher) {
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
        val userMap = result?.toMap()
        UserDto.fromMap(userMap)
    }

    override suspend fun insertAuth(auth: AuthDto) = withContext(ioDispatcher) {
        val document = MutableDocument(
            auth.id.toString(),
            auth.toMap()
        )

        authCollection.save(document)
    }

    override suspend fun logout() = withContext(ioDispatcher) {
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

    override suspend fun isAuthenticated(): Boolean = withContext(ioDispatcher) {
        val query = QueryBuilder
            .select(SelectResult.expression(Expression.property("id")))
            .from(DataSource.collection(authCollection))
            .limit(Expression.intValue(1))

        val resultSet = query.execute()
        resultSet.allResults().isNotEmpty()
    }

    override suspend fun getAuth(): AuthDto? = withContext(ioDispatcher) {
        val query = QueryBuilder
            .select(SelectResult.all())
            .from(DataSource.collection(authCollection))
            .limit(Expression.intValue(1))

        val resultSet = query.execute()
        val result = resultSet.allResults().firstOrNull()
        val authMap = result?.toMap()
        AuthDto.fromMap(authMap)
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