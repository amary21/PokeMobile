package com.amary.poke.mobile.domain.usecase

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
class LoginUseCaseTest {

    private lateinit var repository: PokeRepository
    private lateinit var loginUseCase: LoginUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        loginUseCase = LoginUseCase(repository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke should return success when login credentials are correct`() = runTest {
        // Given
        val username = "testuser"
        val password = "password123"
        val user = UserModel(
            id = "1",
            userName = username,
            fullName = "Test User",
            email = "test@example.com",
            password = password
        )
        coEvery { repository.login(username, password) } returns user
        coEvery { repository.insertAuth(any()) } returns Unit

        // When
        val result = loginUseCase(username, password)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Login successful", result.getOrNull())
        coVerify { repository.login(username, password) }
        coVerify { repository.insertAuth(AuthModel(id = "1")) }
    }

    @Test
    fun `invoke should return failure when user not found`() = runTest {
        // Given
        val username = "wronguser"
        val password = "wrongpassword"
        coEvery { repository.login(username, password) } returns null

        // When
        val result = loginUseCase(username, password)

        // Then
        assertTrue(result.isFailure)
        assertEquals("User not found", result.exceptionOrNull()?.message)
        coVerify { repository.login(username, password) }
        coVerify(exactly = 0) { repository.insertAuth(any()) }
    }

    @Test
    fun `invoke should insert auth after successful login`() = runTest {
        // Given
        val username = "testuser"
        val password = "password123"
        val user = UserModel(id = "123", userName = username)
        coEvery { repository.login(username, password) } returns user
        coEvery { repository.insertAuth(any()) } returns Unit

        // When
        loginUseCase(username, password)

        // Then
        coVerify { repository.insertAuth(AuthModel(id = "123")) }
    }

    @Test
    fun `invoke should return failure when repository throws exception`() = runTest {
        // Given
        val username = "testuser"
        val password = "password123"
        val expectedException = Exception("Database error")
        coEvery { repository.login(username, password) } throws expectedException

        // When
        val result = loginUseCase(username, password)

        // Then
        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
        coVerify { repository.login(username, password) }
    }

    @Test
    fun `invoke should handle empty username`() = runTest {
        // Given
        val username = ""
        val password = "password123"
        coEvery { repository.login(username, password) } returns null

        // When
        val result = loginUseCase(username, password)

        // Then
        assertTrue(result.isFailure)
        assertEquals("User not found", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke should handle empty password`() = runTest {
        // Given
        val username = "testuser"
        val password = ""
        coEvery { repository.login(username, password) } returns null

        // When
        val result = loginUseCase(username, password)

        // Then
        assertTrue(result.isFailure)
        assertEquals("User not found", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke should call repository login exactly once`() = runTest {
        // Given
        val username = "testuser"
        val password = "password123"
        val user = UserModel(id = "1", userName = username)
        coEvery { repository.login(username, password) } returns user
        coEvery { repository.insertAuth(any()) } returns Unit

        // When
        loginUseCase(username, password)

        // Then
        coVerify(exactly = 1) { repository.login(username, password) }
    }

    @Test
    fun `invoke should execute on the provided dispatcher`() = runTest {
        // Given
        val username = "testuser"
        val password = "password123"
        val user = UserModel(id = "1", userName = username)
        coEvery { repository.login(username, password) } returns user
        coEvery { repository.insertAuth(any()) } returns Unit

        // When
        val result = loginUseCase(username, password)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Login successful", result.getOrNull())
    }

    @Test
    fun `invoke should handle RuntimeException from repository`() = runTest {
        // Given
        val username = "testuser"
        val password = "password123"
        val expectedException = RuntimeException("Unexpected error")
        coEvery { repository.login(username, password) } throws expectedException

        // When
        val result = loginUseCase(username, password)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
        assertEquals("Unexpected error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke should not insert auth when login fails`() = runTest {
        // Given
        val username = "testuser"
        val password = "wrongpassword"
        coEvery { repository.login(username, password) } returns null

        // When
        loginUseCase(username, password)

        // Then
        coVerify(exactly = 0) { repository.insertAuth(any()) }
    }
}

