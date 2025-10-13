package com.amary.poke.mobile.presentation.login

import app.cash.turbine.test
import com.amary.poke.mobile.domain.usecase.LoginUseCase
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
class LoginViewModelTest {

    private lateinit var loginUseCase: LoginUseCase
    private lateinit var viewModel: LoginViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        loginUseCase = mockk()
        viewModel = LoginViewModel(loginUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login should emit loading, loadingComplete and success events when credentials are valid`() = runTest {
        // Given
        val username = "testuser"
        val password = "password123"
        coEvery { loginUseCase.invoke(username, password) } returns Result.success("Login successful")

        // When
        viewModel.events.test {
            viewModel.login(username, password)
            advanceUntilIdle()

            // Then
            val loadingEvent = awaitItem()
            assertTrue(loadingEvent is LoginEvent.Loading)

            val loadingCompleteEvent = awaitItem()
            assertTrue(loadingCompleteEvent is LoginEvent.LoadingComplete)

            val successEvent = awaitItem()
            assertTrue(successEvent is LoginEvent.Success)

            cancelAndIgnoreRemainingEvents()
        }
        coVerify { loginUseCase.invoke(username, password) }
    }

    @Test
    fun `login should emit error event when username is blank`() = runTest {
        // Given
        val username = ""
        val password = "password123"

        // When
        viewModel.events.test {
            viewModel.login(username, password)
            advanceUntilIdle()

            // Then
            val loadingEvent = awaitItem()
            assertTrue(loadingEvent is LoginEvent.Loading)

            val loadingCompleteEvent = awaitItem()
            assertTrue(loadingCompleteEvent is LoginEvent.LoadingComplete)

            val errorEvent = awaitItem()
            assertTrue(errorEvent is LoginEvent.Error)
            assertEquals("Username and password cannot be empty", (errorEvent as LoginEvent.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 0) { loginUseCase.invoke(any(), any()) }
    }

    @Test
    fun `login should emit error event when password is blank`() = runTest {
        // Given
        val username = "testuser"
        val password = ""

        // When
        viewModel.events.test {
            viewModel.login(username, password)
            advanceUntilIdle()

            // Then
            val loadingEvent = awaitItem()
            assertTrue(loadingEvent is LoginEvent.Loading)

            val loadingCompleteEvent = awaitItem()
            assertTrue(loadingCompleteEvent is LoginEvent.LoadingComplete)

            val errorEvent = awaitItem()
            assertTrue(errorEvent is LoginEvent.Error)
            assertEquals("Username and password cannot be empty", (errorEvent as LoginEvent.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 0) { loginUseCase.invoke(any(), any()) }
    }

    @Test
    fun `login should emit error event when both username and password are blank`() = runTest {
        // Given
        val username = ""
        val password = ""

        // When
        viewModel.events.test {
            viewModel.login(username, password)
            advanceUntilIdle()

            // Then
            val loadingEvent = awaitItem()
            assertTrue(loadingEvent is LoginEvent.Loading)

            val loadingCompleteEvent = awaitItem()
            assertTrue(loadingCompleteEvent is LoginEvent.LoadingComplete)

            val errorEvent = awaitItem()
            assertTrue(errorEvent is LoginEvent.Error)
            assertEquals("Username and password cannot be empty", (errorEvent as LoginEvent.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 0) { loginUseCase.invoke(any(), any()) }
    }

    @Test
    fun `login should emit error event when use case returns failure`() = runTest {
        // Given
        val username = "testuser"
        val password = "wrongpassword"
        val errorMessage = "Invalid credentials"
        coEvery { loginUseCase.invoke(username, password) } returns Result.failure(Exception(errorMessage))

        // When
        viewModel.events.test {
            viewModel.login(username, password)
            advanceUntilIdle()

            // Then
            val loadingEvent = awaitItem()
            assertTrue(loadingEvent is LoginEvent.Loading)

            val loadingCompleteEvent = awaitItem()
            assertTrue(loadingCompleteEvent is LoginEvent.LoadingComplete)

            val errorEvent = awaitItem()
            assertTrue(errorEvent is LoginEvent.Error)
            assertEquals(errorMessage, (errorEvent as LoginEvent.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
        coVerify { loginUseCase.invoke(username, password) }
    }

    @Test
    fun `login should emit error event with default message when exception message is null`() = runTest {
        // Given
        val username = "testuser"
        val password = "password123"
        coEvery { loginUseCase.invoke(username, password) } returns Result.failure(Exception())

        // When
        viewModel.events.test {
            viewModel.login(username, password)
            advanceUntilIdle()

            // Then
            val loadingEvent = awaitItem()
            assertTrue(loadingEvent is LoginEvent.Loading)

            val loadingCompleteEvent = awaitItem()
            assertTrue(loadingCompleteEvent is LoginEvent.LoadingComplete)

            val errorEvent = awaitItem()
            assertTrue(errorEvent is LoginEvent.Error)
            assertEquals("Unknown error", (errorEvent as LoginEvent.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
        coVerify { loginUseCase.invoke(username, password) }
    }

    @Test
    fun `login should handle whitespace username as blank`() = runTest {
        // Given
        val username = "   "
        val password = "password123"

        // When
        viewModel.events.test {
            viewModel.login(username, password)
            advanceUntilIdle()

            // Then
            val loadingEvent = awaitItem()
            assertTrue(loadingEvent is LoginEvent.Loading)

            val loadingCompleteEvent = awaitItem()
            assertTrue(loadingCompleteEvent is LoginEvent.LoadingComplete)

            val errorEvent = awaitItem()
            assertTrue(errorEvent is LoginEvent.Error)
            assertEquals("Username and password cannot be empty", (errorEvent as LoginEvent.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 0) { loginUseCase.invoke(any(), any()) }
    }

    @Test
    fun `login should call use case with correct parameters`() = runTest {
        // Given
        val username = "johndoe"
        val password = "securepass"
        coEvery { loginUseCase.invoke(username, password) } returns Result.success("Login successful")

        // When
        viewModel.login(username, password)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { loginUseCase.invoke(username, password) }
    }
}
