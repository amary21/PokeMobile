package com.amary.poke.mobile.domain.usecase

import app.cash.turbine.test
import com.amary.poke.mobile.domain.model.AuthModel
import com.amary.poke.mobile.domain.model.UserModel
import com.amary.poke.mobile.domain.repository.PokeRepository
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
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileUseCaseTest {

    private lateinit var repository: PokeRepository
    private lateinit var profileUseCase: ProfileUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        profileUseCase = ProfileUseCase(repository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke should emit success with user data when auth and user exist`() = runTest {
        // Given
        val auth = AuthModel(id = "1")
        val user = UserModel(
            id = "1",
            userName = "testuser",
            fullName = "Test User",
            email = "test@example.com",
            password = "password123"
        )
        coEvery { repository.getAuth() } returns auth
        coEvery { repository.getUserById("1") } returns user

        // When
        val flow = profileUseCase()

        // Then
        flow.test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals("testuser", result.getOrNull()?.userName)
            assertEquals("Test User", result.getOrNull()?.fullName)
            assertEquals("test@example.com", result.getOrNull()?.email)
            awaitComplete()
        }
        coVerify { repository.getAuth() }
        coVerify { repository.getUserById("1") }
    }

    @Test
    fun `invoke should emit failure when auth not found`() = runTest {
        // Given
        coEvery { repository.getAuth() } returns null

        // When
        val flow = profileUseCase()

        // Then
        flow.test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertEquals("Auth not found", result.exceptionOrNull()?.message)
            awaitComplete()
        }
        coVerify { repository.getAuth() }
        coVerify(exactly = 0) { repository.getUserById(any()) }
    }

    @Test
    fun `invoke should emit failure when user not found`() = runTest {
        // Given
        val auth = AuthModel(id = "999")
        coEvery { repository.getAuth() } returns auth
        coEvery { repository.getUserById("999") } returns null

        // When
        val flow = profileUseCase()

        // Then
        flow.test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertEquals("User not found", result.exceptionOrNull()?.message)
            awaitComplete()
        }
        coVerify { repository.getAuth() }
        coVerify { repository.getUserById("999") }
    }

    @Test
    fun `invoke should emit failure when repository throws exception`() = runTest {
        // Given
        val expectedException = Exception("Database error")
        coEvery { repository.getAuth() } throws expectedException

        // When
        val flow = profileUseCase()

        // Then
        flow.test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertEquals(expectedException, result.exceptionOrNull())
            awaitComplete()
        }
        coVerify { repository.getAuth() }
    }

    @Test
    fun `invoke should call getAuth and getUserById in sequence`() = runTest {
        // Given
        val auth = AuthModel(id = "1")
        val user = UserModel(id = "1", userName = "testuser")
        coEvery { repository.getAuth() } returns auth
        coEvery { repository.getUserById("1") } returns user

        // When
        val flow = profileUseCase()

        // Then
        flow.test {
            awaitItem()
            awaitComplete()
        }
        coVerify(exactly = 1) { repository.getAuth() }
        coVerify(exactly = 1) { repository.getUserById("1") }
    }

    @Test
    fun `invoke should emit complete user profile data`() = runTest {
        // Given
        val auth = AuthModel(id = "123")
        val user = UserModel(
            id = "123",
            userName = "johndoe",
            fullName = "John Doe",
            email = "john@example.com",
            password = "secret123"
        )
        coEvery { repository.getAuth() } returns auth
        coEvery { repository.getUserById("123") } returns user

        // When
        val flow = profileUseCase()

        // Then
        flow.test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            val profile = result.getOrNull()!!
            assertEquals("123", profile.id)
            assertEquals("johndoe", profile.userName)
            assertEquals("John Doe", profile.fullName)
            assertEquals("john@example.com", profile.email)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should execute on the provided dispatcher`() = runTest {
        // Given
        val auth = AuthModel(id = "1")
        val user = UserModel(id = "1", userName = "testuser")
        coEvery { repository.getAuth() } returns auth
        coEvery { repository.getUserById("1") } returns user

        // When
        val flow = profileUseCase()

        // Then
        flow.test {
            testDispatcher.scheduler.advanceUntilIdle()
            val result = awaitItem()
            assertTrue(result.isSuccess)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should handle RuntimeException from repository`() = runTest {
        // Given
        val expectedException = RuntimeException("Unexpected error")
        coEvery { repository.getAuth() } throws expectedException

        // When
        val flow = profileUseCase()

        // Then
        flow.test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is RuntimeException)
            assertEquals("Unexpected error", result.exceptionOrNull()?.message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should emit failure when getUserById throws exception`() = runTest {
        // Given
        val auth = AuthModel(id = "1")
        val expectedException = Exception("User fetch failed")
        coEvery { repository.getAuth() } returns auth
        coEvery { repository.getUserById("1") } throws expectedException

        // When
        val flow = profileUseCase()

        // Then
        flow.test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertEquals("User fetch failed", result.exceptionOrNull()?.message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should complete flow after emitting single item`() = runTest {
        // Given
        val auth = AuthModel(id = "1")
        val user = UserModel(id = "1", userName = "testuser")
        coEvery { repository.getAuth() } returns auth
        coEvery { repository.getUserById("1") } returns user

        // When
        val flow = profileUseCase()

        // Then
        flow.test {
            assertNotNull(awaitItem())
            awaitComplete()
        }
    }
}

