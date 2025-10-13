package com.amary.poke.mobile.domain.usecase

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
class RegisterUseCaseTest {

    private lateinit var repository: PokeRepository
    private lateinit var registerUseCase: RegisterUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        registerUseCase = RegisterUseCase(repository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke should return success when registration is successful`() = runTest {
        // Given
        val username = "newuser"
        val fullName = "New User"
        val email = "newuser@example.com"
        val password = "password123"
        coEvery { repository.isUsernameExists(username) } returns false andThen true
        coEvery { repository.insertUser(any()) } returns Unit

        // When
        val result = registerUseCase(username, fullName, email, password)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Registration successful", result.getOrNull())
        coVerify { repository.isUsernameExists(username) }
        coVerify { repository.insertUser(any()) }
    }

    @Test
    fun `invoke should return failure when username already exists`() = runTest {
        // Given
        val username = "existinguser"
        val fullName = "Existing User"
        val email = "existing@example.com"
        val password = "password123"
        coEvery { repository.isUsernameExists(username) } returns true

        // When
        val result = registerUseCase(username, fullName, email, password)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Username already exists", result.exceptionOrNull()?.message)
        coVerify { repository.isUsernameExists(username) }
        coVerify(exactly = 0) { repository.insertUser(any()) }
    }

    @Test
    fun `invoke should return failure when registration fails after insert`() = runTest {
        // Given
        val username = "newuser"
        val fullName = "New User"
        val email = "newuser@example.com"
        val password = "password123"
        coEvery { repository.isUsernameExists(username) } returns false andThen false
        coEvery { repository.insertUser(any()) } returns Unit

        // When
        val result = registerUseCase(username, fullName, email, password)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Registration failed", result.exceptionOrNull()?.message)
        coVerify(exactly = 2) { repository.isUsernameExists(username) }
        coVerify { repository.insertUser(any()) }
    }

    @Test
    fun `invoke should insert user with correct data`() = runTest {
        // Given
        val username = "testuser"
        val fullName = "Test User"
        val email = "test@example.com"
        val password = "password123"
        coEvery { repository.isUsernameExists(username) } returns false andThen true
        coEvery { repository.insertUser(any()) } returns Unit

        // When
        registerUseCase(username, fullName, email, password)

        // Then
        coVerify {
            repository.insertUser(match { user ->
                user.userName == username &&
                user.fullName == fullName &&
                user.email == email &&
                user.password == password
            })
        }
    }

    @Test
    fun `invoke should return failure when repository throws exception`() = runTest {
        // Given
        val username = "testuser"
        val fullName = "Test User"
        val email = "test@example.com"
        val password = "password123"
        val expectedException = Exception("Database error")
        coEvery { repository.isUsernameExists(username) } throws expectedException

        // When
        val result = registerUseCase(username, fullName, email, password)

        // Then
        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
        coVerify { repository.isUsernameExists(username) }
    }

    @Test
    fun `invoke should check username existence twice`() = runTest {
        // Given
        val username = "newuser"
        val fullName = "New User"
        val email = "new@example.com"
        val password = "password123"
        coEvery { repository.isUsernameExists(username) } returns false andThen true
        coEvery { repository.insertUser(any()) } returns Unit

        // When
        registerUseCase(username, fullName, email, password)

        // Then
        coVerify(exactly = 2) { repository.isUsernameExists(username) }
    }

    @Test
    fun `invoke should handle empty username`() = runTest {
        // Given
        val username = ""
        val fullName = "User"
        val email = "user@example.com"
        val password = "password123"
        coEvery { repository.isUsernameExists(username) } returns false

        // When
        val result = registerUseCase(username, fullName, email, password)

        // Then
        // Still processes but verifies empty username behavior
        coVerify { repository.isUsernameExists(username) }
    }

    @Test
    fun `invoke should execute on the provided dispatcher`() = runTest {
        // Given
        val username = "testuser"
        val fullName = "Test User"
        val email = "test@example.com"
        val password = "password123"
        coEvery { repository.isUsernameExists(username) } returns false andThen true
        coEvery { repository.insertUser(any()) } returns Unit

        // When
        val result = registerUseCase(username, fullName, email, password)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Registration successful", result.getOrNull())
    }

    @Test
    fun `invoke should handle RuntimeException from repository`() = runTest {
        // Given
        val username = "testuser"
        val fullName = "Test User"
        val email = "test@example.com"
        val password = "password123"
        val expectedException = RuntimeException("Unexpected error")
        coEvery { repository.isUsernameExists(username) } throws expectedException

        // When
        val result = registerUseCase(username, fullName, email, password)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
        assertEquals("Unexpected error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke should not insert user when username exists`() = runTest {
        // Given
        val username = "existinguser"
        val fullName = "Existing User"
        val email = "existing@example.com"
        val password = "password123"
        coEvery { repository.isUsernameExists(username) } returns true

        // When
        registerUseCase(username, fullName, email, password)

        // Then
        coVerify(exactly = 0) { repository.insertUser(any()) }
    }

    @Test
    fun `invoke should handle insertUser exception`() = runTest {
        // Given
        val username = "newuser"
        val fullName = "New User"
        val email = "new@example.com"
        val password = "password123"
        val expectedException = Exception("Insert failed")
        coEvery { repository.isUsernameExists(username) } returns false
        coEvery { repository.insertUser(any()) } throws expectedException

        // When
        val result = registerUseCase(username, fullName, email, password)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Insert failed", result.exceptionOrNull()?.message)
    }
}

