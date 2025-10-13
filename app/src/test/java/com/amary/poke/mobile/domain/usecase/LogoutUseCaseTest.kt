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
class LogoutUseCaseTest {

    private lateinit var repository: PokeRepository
    private lateinit var logoutUseCase: LogoutUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        logoutUseCase = LogoutUseCase(repository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke should return success when logout is successful`() = runTest {
        // Given
        coEvery { repository.logout() } returns Unit

        // When
        val result = logoutUseCase()

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Logout successful", result.getOrNull())
        coVerify { repository.logout() }
    }

    @Test
    fun `invoke should call repository logout exactly once`() = runTest {
        // Given
        coEvery { repository.logout() } returns Unit

        // When
        logoutUseCase()

        // Then
        coVerify(exactly = 1) { repository.logout() }
    }

    @Test
    fun `invoke should return failure when repository throws exception`() = runTest {
        // Given
        val expectedException = Exception("Logout failed")
        coEvery { repository.logout() } throws expectedException

        // When
        val result = logoutUseCase()

        // Then
        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
        assertEquals("Logout failed", result.exceptionOrNull()?.message)
        coVerify { repository.logout() }
    }

    @Test
    fun `invoke should return failure when repository throws RuntimeException`() = runTest {
        // Given
        val expectedException = RuntimeException("Database error")
        coEvery { repository.logout() } throws expectedException

        // When
        val result = logoutUseCase()

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
        assertEquals("Database error", result.exceptionOrNull()?.message)
        coVerify { repository.logout() }
    }

    @Test
    fun `invoke should execute on the provided dispatcher`() = runTest {
        // Given
        coEvery { repository.logout() } returns Unit

        // When
        val result = logoutUseCase()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(result.isSuccess)
        assertEquals("Logout successful", result.getOrNull())
        coVerify { repository.logout() }
    }

    @Test
    fun `invoke should handle multiple consecutive logout calls`() = runTest {
        // Given
        coEvery { repository.logout() } returns Unit

        // When
        val result1 = logoutUseCase()
        val result2 = logoutUseCase()

        // Then
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)
        coVerify(exactly = 2) { repository.logout() }
    }

    @Test
    fun `invoke should return failure with proper exception message`() = runTest {
        // Given
        val errorMessage = "Auth service unavailable"
        val expectedException = Exception(errorMessage)
        coEvery { repository.logout() } throws expectedException

        // When
        val result = logoutUseCase()

        // Then
        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
        coVerify { repository.logout() }
    }

    @Test
    fun `invoke should handle IOException from repository`() = runTest {
        // Given
        val expectedException = java.io.IOException("Connection error")
        coEvery { repository.logout() } throws expectedException

        // When
        val result = logoutUseCase()

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is java.io.IOException)
        assertEquals("Connection error", result.exceptionOrNull()?.message)
        coVerify { repository.logout() }
    }
}

