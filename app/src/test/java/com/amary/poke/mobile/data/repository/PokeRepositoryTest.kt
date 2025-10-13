package com.amary.poke.mobile.data.repository

import com.amary.poke.mobile.data.local.dto.AuthDto
import com.amary.poke.mobile.data.local.dto.ResultDto
import com.amary.poke.mobile.data.local.dto.UserDto
import com.amary.poke.mobile.data.local.source.LocalSource
import com.amary.poke.mobile.data.remote.api.PokeApi
import com.amary.poke.mobile.data.remote.response.DetailResponse
import com.amary.poke.mobile.data.remote.response.PokeResponse
import com.amary.poke.mobile.data.remote.response.ResultResponse
import com.amary.poke.mobile.domain.model.AuthModel
import com.amary.poke.mobile.domain.model.ResultModel
import com.amary.poke.mobile.domain.model.UserModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class PokeRepositoryTest {

    private lateinit var pokeApi: PokeApi
    private lateinit var localSource: LocalSource
    private lateinit var repository: PokeRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        pokeApi = mockk()
        localSource = mockk()
        repository = PokeRepositoryImpl(pokeApi, localSource, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `listPokemon should return PokeModel when API call is successful`() = runTest {
        // Given
        val limit = 20
        val offset = 0
        val mockResponse = PokeResponse(
            count = 100,
            next = "https://pokeapi.co/api/v2/pokemon?offset=20&limit=20",
            previous = null,
            result = listOf(
                ResultResponse(name = "bulbasaur", url = "https://pokeapi.co/api/v2/pokemon/1/"),
                ResultResponse(name = "ivysaur", url = "https://pokeapi.co/api/v2/pokemon/2/")
            )
        )
        coEvery { pokeApi.getPokemon(limit, offset) } returns mockResponse

        // When
        val result = repository.listPokemon(limit, offset)

        // Then
        assertNotNull(result)
        assertEquals(20, result.next)
        assertEquals(2, result.result.size)
        assertEquals("bulbasaur", result.result[0].name)
        assertEquals("ivysaur", result.result[1].name)
        coVerify { pokeApi.getPokemon(limit, offset) }
    }

    @Test
    fun `listPokemon should return correct offset from next URL`() = runTest {
        // Given
        val limit = 10
        val offset = 10
        val mockResponse = PokeResponse(
            count = 100,
            next = "https://pokeapi.co/api/v2/pokemon?offset=30&limit=10",
            previous = null,
            result = emptyList()
        )
        coEvery { pokeApi.getPokemon(limit, offset) } returns mockResponse

        // When
        val result = repository.listPokemon(limit, offset)

        // Then
        assertEquals(30, result.next)
        coVerify { pokeApi.getPokemon(limit, offset) }
    }

    @Test
    fun `listPokemon should return default offset when next URL is null`() = runTest {
        // Given
        val limit = 20
        val offset = 0
        val mockResponse = PokeResponse(
            count = 100,
            next = null,
            previous = null,
            result = emptyList()
        )
        coEvery { pokeApi.getPokemon(limit, offset) } returns mockResponse

        // When
        val result = repository.listPokemon(limit, offset)

        // Then
        assertEquals(10, result.next)
        coVerify { pokeApi.getPokemon(limit, offset) }
    }

    @Test
    fun `getPokemonDetail should return DetailModel when API call is successful`() = runTest {
        // Given
        val pokemonName = "pikachu"
        val mockResponse = DetailResponse(
            abilities = emptyList(),
            baseExperience = 112,
            height = 4,
            id = 25,
            isDefault = true,
            locationAreaEncounters = "https://pokeapi.co/api/v2/pokemon/25/encounters",
            name = "pikachu",
            order = 35,
            weight = 60
        )
        coEvery { pokeApi.getPokemonDetail(pokemonName) } returns mockResponse

        // When
        val result = repository.getPokemonDetail(pokemonName)

        // Then
        assertNotNull(result)
        assertEquals("pikachu", result.name)
        assertEquals(25, result.id)
        assertEquals(112, result.baseExperience)
        assertEquals(4, result.height)
        assertEquals(60, result.weight)
        coVerify { pokeApi.getPokemonDetail(pokemonName) }
    }

    @Test
    fun `listLocalPokemon should return list of ResultModel from local database`() = runTest {
        // Given
        val mockDtoList = listOf(
            ResultDto(name = "bulbasaur", url = "url1"),
            ResultDto(name = "ivysaur", url = "url2"),
            ResultDto(name = "venusaur", url = "url3")
        )
        coEvery { localSource.getAll() } returns mockDtoList

        // When
        val result = repository.listLocalPokemon()

        // Then
        assertEquals(3, result.size)
        assertEquals("bulbasaur", result[0].name)
        assertEquals("ivysaur", result[1].name)
        assertEquals("venusaur", result[2].name)
        coVerify { localSource.getAll() }
    }

    @Test
    fun `listLocalPokemon should return empty list when database is empty`() = runTest {
        // Given
        coEvery { localSource.getAll() } returns emptyList()

        // When
        val result = repository.listLocalPokemon()

        // Then
        assertTrue(result.isEmpty())
        coVerify { localSource.getAll() }
    }

    @Test
    fun `savePokemon should insert pokemon to local database`() = runTest {
        // Given
        val pokemonList = listOf(
            ResultModel(name = "charmander", url = "url1"),
            ResultModel(name = "charmeleon", url = "url2")
        )
        coEvery { localSource.insert(any()) } returns Unit

        // When
        repository.savePokemon(pokemonList)

        // Then
        coVerify { localSource.insert(any()) }
    }

    @Test
    fun `savePokemon should insert pokemon to local database with empty list`() = runTest {
        // Given
        coEvery { localSource.insert(any()) } returns Unit

        // When
        repository.savePokemon(emptyList())

        // Then
        coVerify { localSource.insert(any()) }
    }

    @Test
    fun `deletePokemon should delete all pokemon from local database`() = runTest {
        // Given
        coEvery { localSource.deleteAll() } returns Unit

        // When
        repository.deletePokemon()

        // Then
        coVerify { localSource.deleteAll() }
    }

    @Test
    fun `isUsernameExists should return true when username exists`() = runTest {
        // Given
        val username = "testuser"
        coEvery { localSource.isUsernameExists(username) } returns true

        // When
        val result = repository.isUsernameExists(username)

        // Then
        assertTrue(result)
        coVerify { localSource.isUsernameExists(username) }
    }

    @Test
    fun `isUsernameExists should return false when username does not exist`() = runTest {
        // Given
        val username = "nonexistent"
        coEvery { localSource.isUsernameExists(username) } returns false

        // When
        val result = repository.isUsernameExists(username)

        // Then
        assertFalse(result)
        coVerify { localSource.isUsernameExists(username) }
    }

    @Test
    fun `insertUser should insert user to local database`() = runTest {
        // Given
        val user = UserModel(
            id = "1",
            userName = "testuser",
            fullName = "Test User",
            email = "test@example.com",
            password = "password123"
        )
        coEvery { localSource.insertUser(any()) } returns Unit

        // When
        repository.insertUser(user)

        // Then
        coVerify { localSource.insertUser(any()) }
    }

    @Test
    fun `login should return UserModel when credentials are correct`() = runTest {
        // Given
        val username = "testuser"
        val password = "password123"
        val mockUserDto = UserDto(
            id = "1",
            userName = username,
            fullName = "Test User",
            email = "test@example.com",
            password = password
        )
        coEvery { localSource.login(username, password) } returns mockUserDto

        // When
        val result = repository.login(username, password)

        // Then
        assertNotNull(result)
        assertEquals("1", result?.id)
        assertEquals(username, result?.userName)
        assertEquals("Test User", result?.fullName)
        coVerify { localSource.login(username, password) }
    }

    @Test
    fun `login should return null when credentials are incorrect`() = runTest {
        // Given
        val username = "testuser"
        val password = "wrongpassword"
        coEvery { localSource.login(username, password) } returns null

        // When
        val result = repository.login(username, password)

        // Then
        assertNull(result)
        coVerify { localSource.login(username, password) }
    }

    @Test
    fun `getUserById should return UserModel when user exists`() = runTest {
        // Given
        val userId = "1"
        val mockUserDto = UserDto(
            id = userId,
            userName = "testuser",
            fullName = "Test User",
            email = "test@example.com",
            password = "password123"
        )
        coEvery { localSource.getUserById(userId) } returns mockUserDto

        // When
        val result = repository.getUserById(userId)

        // Then
        assertNotNull(result)
        assertEquals(userId, result?.id)
        assertEquals("testuser", result?.userName)
        coVerify { localSource.getUserById(userId) }
    }

    @Test
    fun `getUserById should return null when user does not exist`() = runTest {
        // Given
        val userId = "999"
        coEvery { localSource.getUserById(userId) } returns null

        // When
        val result = repository.getUserById(userId)

        // Then
        assertNull(result)
        coVerify { localSource.getUserById(userId) }
    }

    @Test
    fun `insertAuth should insert auth to local database`() = runTest {
        // Given
        val auth = AuthModel(id = "1")
        coEvery { localSource.insertAuth(any()) } returns Unit

        // When
        repository.insertAuth(auth)

        // Then
        coVerify { localSource.insertAuth(any()) }
    }

    @Test
    fun `logout should clear auth from local database`() = runTest {
        // Given
        coEvery { localSource.logout() } returns Unit

        // When
        repository.logout()

        // Then
        coVerify { localSource.logout() }
    }

    @Test
    fun `isAuthenticated should return true when user is authenticated`() = runTest {
        // Given
        coEvery { localSource.isAuthenticated() } returns true

        // When
        val result = repository.isAuthenticated()

        // Then
        assertTrue(result)
        coVerify { localSource.isAuthenticated() }
    }

    @Test
    fun `isAuthenticated should return false when user is not authenticated`() = runTest {
        // Given
        coEvery { localSource.isAuthenticated() } returns false

        // When
        val result = repository.isAuthenticated()

        // Then
        assertFalse(result)
        coVerify { localSource.isAuthenticated() }
    }

    @Test
    fun `getAuth should return AuthModel when auth exists`() = runTest {
        // Given
        val mockAuthDto = AuthDto(id = "1")
        coEvery { localSource.getAuth() } returns mockAuthDto

        // When
        val result = repository.getAuth()

        // Then
        assertNotNull(result)
        assertEquals("1", result?.id)
        coVerify { localSource.getAuth() }
    }

    @Test
    fun `getAuth should return null when auth does not exist`() = runTest {
        // Given
        coEvery { localSource.getAuth() } returns null

        // When
        val result = repository.getAuth()

        // Then
        assertNull(result)
        coVerify { localSource.getAuth() }
    }
}