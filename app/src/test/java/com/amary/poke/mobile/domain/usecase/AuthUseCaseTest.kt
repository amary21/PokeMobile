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
class AuthUseCaseTest {

    private lateinit var repository: PokeRepository
    private lateinit var authUseCase: AuthUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        authUseCase = AuthUseCase(repository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke should return success with true when user is authenticated`() = runTest {
        // Given
        coEvery { repository.isAuthenticated() } returns true

        // When
        val result = authUseCase()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
        coVerify { repository.isAuthenticated() }
    }

    @Test
    fun `invoke should return success with false when user is not authenticated`() = runTest {
        // Given
        coEvery { repository.isAuthenticated() } returns false

        // When
        val result = authUseCase()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(false, result.getOrNull())
        coVerify { repository.isAuthenticated() }
    }

    @Test
    fun `invoke should return failure when repository throws exception`() = runTest {
        // Given
        val expectedException = Exception("Database error")
        coEvery { repository.isAuthenticated() } throws expectedException

        // When
        val result = authUseCase()

        // Then
        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
        coVerify { repository.isAuthenticated() }
    }

    @Test
    fun `invoke should return failure when repository throws RuntimeException`() = runTest {
        // Given
        val expectedException = RuntimeException("Unexpected error")
        coEvery { repository.isAuthenticated() } throws expectedException

        // When
        val result = authUseCase()

        // Then
        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
        assertEquals("Unexpected error", result.exceptionOrNull()?.message)
        coVerify { repository.isAuthenticated() }
    }

    @Test
    fun `invoke should call repository isAuthenticated exactly once`() = runTest {
        // Given
        coEvery { repository.isAuthenticated() } returns true

        // When
        authUseCase()

        // Then
        coVerify(exactly = 1) { repository.isAuthenticated() }
    }

    @Test
    fun `invoke should handle multiple consecutive calls correctly`() = runTest {
        // Given
        coEvery { repository.isAuthenticated() } returns true andThen false

        // When
        val result1 = authUseCase()
        val result2 = authUseCase()

        // Then
        assertTrue(result1.isSuccess)
        assertEquals(true, result1.getOrNull())
        assertTrue(result2.isSuccess)
        assertEquals(false, result2.getOrNull())
        coVerify(exactly = 2) { repository.isAuthenticated() }
    }

    @Test
    fun `invoke should return failure with proper exception message`() = runTest {
        // Given
        val errorMessage = "Authentication service unavailable"
        val expectedException = Exception(errorMessage)
        coEvery { repository.isAuthenticated() } throws expectedException

        // When
        val result = authUseCase()

        // Then
        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
        coVerify { repository.isAuthenticated() }
    }

    @Test
    fun `invoke should execute on the provided dispatcher`() = runTest {
        // Given
        coEvery { repository.isAuthenticated() } returns true

        // When
        val result = authUseCase()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
        coVerify { repository.isAuthenticated() }
    }
}