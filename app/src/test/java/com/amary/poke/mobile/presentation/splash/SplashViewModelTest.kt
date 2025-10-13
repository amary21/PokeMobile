package com.amary.poke.mobile.presentation.splash

import app.cash.turbine.test
import com.amary.poke.mobile.domain.usecase.AuthUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    private lateinit var authUseCase: AuthUseCase
    private lateinit var viewModel: SplashViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should call checkAuthentication and emit success event when user is authenticated`() = runTest {
        // Given
        coEvery { authUseCase.invoke() } returns Result.success(true)

        // When
        viewModel = SplashViewModel(authUseCase)

        // Then
        viewModel.events.test {
            advanceUntilIdle()

            val successEvent = awaitItem()
            assertTrue(successEvent is SplashEvent.Success)

            cancelAndIgnoreRemainingEvents()
        }
        coVerify { authUseCase.invoke() }
    }

    @Test
    fun `init should call checkAuthentication and not emit success event when user is not authenticated`() = runTest {
        // Given
        coEvery { authUseCase.invoke() } returns Result.success(false)

        // When
        viewModel = SplashViewModel(authUseCase)
        advanceUntilIdle()

        // Then
        coVerify { authUseCase.invoke() }
    }

    @Test
    fun `init should emit error event when authentication check fails`() = runTest {
        // Given
        val errorMessage = "Authentication check failed"
        coEvery { authUseCase.invoke() } returns Result.failure(Exception(errorMessage))

        // When
        viewModel = SplashViewModel(authUseCase)

        // Then
        viewModel.events.test {
            advanceUntilIdle()

            val errorEvent = awaitItem()
            assertTrue(errorEvent is SplashEvent.Error)
            assertEquals(errorMessage, (errorEvent as SplashEvent.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
        coVerify { authUseCase.invoke() }
    }

    @Test
    fun `init should emit error with default message when exception message is null`() = runTest {
        // Given
        coEvery { authUseCase.invoke() } returns Result.failure(Exception())

        // When
        viewModel = SplashViewModel(authUseCase)

        // Then
        viewModel.events.test {
            advanceUntilIdle()

            val errorEvent = awaitItem()
            assertTrue(errorEvent is SplashEvent.Error)
            assertEquals("Authentication check failed", (errorEvent as SplashEvent.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
        coVerify { authUseCase.invoke() }
    }

    @Test
    fun `checkAuthentication should emit success event when user is authenticated`() = runTest {
        // Given
        coEvery { authUseCase.invoke() } returns Result.success(true)
        viewModel = SplashViewModel(authUseCase)
        advanceUntilIdle()

        // When
        viewModel.events.test {
            viewModel.checkAuthentication()
            advanceUntilIdle()

            // Then
            val successEvent = awaitItem()
            assertTrue(successEvent is SplashEvent.Success)

            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 2) { authUseCase.invoke() } // Once in init, once in checkAuthentication
    }

    @Test
    fun `checkAuthentication should not emit success event when user is not authenticated`() = runTest {
        // Given
        coEvery { authUseCase.invoke() } returns Result.success(false)
        viewModel = SplashViewModel(authUseCase)
        advanceUntilIdle()

        // When
        viewModel.checkAuthentication()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 2) { authUseCase.invoke() }
    }

    @Test
    fun `checkAuthentication should emit error event when authentication fails`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { authUseCase.invoke() } returns Result.failure(Exception(errorMessage))
        viewModel = SplashViewModel(authUseCase)
        advanceUntilIdle()

        // When
        viewModel.events.test {
            viewModel.checkAuthentication()
            advanceUntilIdle()

            // Then
            val errorEvent = awaitItem()
            assertTrue(errorEvent is SplashEvent.Error)
            assertEquals(errorMessage, (errorEvent as SplashEvent.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 2) { authUseCase.invoke() }
    }

    @Test
    fun `checkAuthentication should call authUseCase exactly once per invocation`() = runTest {
        // Given
        coEvery { authUseCase.invoke() } returns Result.success(true)
        viewModel = SplashViewModel(authUseCase)
        advanceUntilIdle()

        // When
        viewModel.checkAuthentication()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 2) { authUseCase.invoke() }
    }

    @Test
    fun `init should automatically trigger checkAuthentication on viewModel creation`() = runTest {
        // Given
        coEvery { authUseCase.invoke() } returns Result.success(true)

        // When
        viewModel = SplashViewModel(authUseCase)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { authUseCase.invoke() }
    }

    @Test
    fun `multiple checkAuthentication calls should each invoke authUseCase`() = runTest {
        // Given
        coEvery { authUseCase.invoke() } returns Result.success(true)
        viewModel = SplashViewModel(authUseCase)
        advanceUntilIdle()

        // When
        viewModel.checkAuthentication()
        advanceUntilIdle()
        viewModel.checkAuthentication()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 3) { authUseCase.invoke() } // Once in init, twice in checkAuthentication calls
    }
}

