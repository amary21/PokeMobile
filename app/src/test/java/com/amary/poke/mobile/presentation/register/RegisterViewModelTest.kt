package com.amary.poke.mobile.presentation.register

import app.cash.turbine.test
import com.amary.poke.mobile.domain.usecase.RegisterUseCase
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
class RegisterViewModelTest {

    private lateinit var registerUseCase: RegisterUseCase
    private lateinit var viewModel: RegisterViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        registerUseCase = mockk()
        viewModel = RegisterViewModel(registerUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `register should emit loading, loadingComplete and success events when all fields are valid`() = runTest {
        // Given
        val username = "johndoe"
        val fullName = "John Doe"
        val email = "john@example.com"
        val password = "password123"
        coEvery { registerUseCase.invoke(username, fullName, email, password) } returns Result.success("Registration successful")

        // When
        viewModel.events.test {
            viewModel.register(username, fullName, email, password)
            advanceUntilIdle()

            // Then
            val loadingEvent = awaitItem()
            assertTrue(loadingEvent is RegisterEvent.Loading)

            val loadingCompleteEvent = awaitItem()
            assertTrue(loadingCompleteEvent is RegisterEvent.LoadingComplete)

            val successEvent = awaitItem()
            assertTrue(successEvent is RegisterEvent.Success)

            cancelAndIgnoreRemainingEvents()
        }
        coVerify { registerUseCase.invoke(username, fullName, email, password) }
    }

    @Test
    fun `register should emit error event when username is blank`() = runTest {
        // Given
        val username = ""
        val fullName = "John Doe"
        val email = "john@example.com"
        val password = "password123"

        // When
        viewModel.events.test {
            viewModel.register(username, fullName, email, password)
            advanceUntilIdle()

            // Then
            val loadingEvent = awaitItem()
            assertTrue(loadingEvent is RegisterEvent.Loading)

            val loadingCompleteEvent = awaitItem()
            assertTrue(loadingCompleteEvent is RegisterEvent.LoadingComplete)

            val errorEvent = awaitItem()
            assertTrue(errorEvent is RegisterEvent.Error)
            assertEquals("All fields are required", (errorEvent as RegisterEvent.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 0) { registerUseCase.invoke(any(), any(), any(), any()) }
    }

    @Test
    fun `register should emit error event when fullName is blank`() = runTest {
        // Given
        val username = "johndoe"
        val fullName = ""
        val email = "john@example.com"
        val password = "password123"

        // When
        viewModel.events.test {
            viewModel.register(username, fullName, email, password)
            advanceUntilIdle()

            // Then
            val loadingEvent = awaitItem()
            assertTrue(loadingEvent is RegisterEvent.Loading)

            val loadingCompleteEvent = awaitItem()
            assertTrue(loadingCompleteEvent is RegisterEvent.LoadingComplete)

            val errorEvent = awaitItem()
            assertTrue(errorEvent is RegisterEvent.Error)
            assertEquals("All fields are required", (errorEvent as RegisterEvent.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 0) { registerUseCase.invoke(any(), any(), any(), any()) }
    }

    @Test
    fun `register should emit error event when email is blank`() = runTest {
        // Given
        val username = "johndoe"
        val fullName = "John Doe"
        val email = ""
        val password = "password123"

        // When
        viewModel.events.test {
            viewModel.register(username, fullName, email, password)
            advanceUntilIdle()

            // Then
            val loadingEvent = awaitItem()
            assertTrue(loadingEvent is RegisterEvent.Loading)

            val loadingCompleteEvent = awaitItem()
            assertTrue(loadingCompleteEvent is RegisterEvent.LoadingComplete)

            val errorEvent = awaitItem()
            assertTrue(errorEvent is RegisterEvent.Error)
            assertEquals("All fields are required", (errorEvent as RegisterEvent.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 0) { registerUseCase.invoke(any(), any(), any(), any()) }
    }

    @Test
    fun `register should emit error event when password is blank`() = runTest {
        // Given
        val username = "johndoe"
        val fullName = "John Doe"
        val email = "john@example.com"
        val password = ""

        // When
        viewModel.events.test {
            viewModel.register(username, fullName, email, password)
            advanceUntilIdle()

            // Then
            val loadingEvent = awaitItem()
            assertTrue(loadingEvent is RegisterEvent.Loading)

            val loadingCompleteEvent = awaitItem()
            assertTrue(loadingCompleteEvent is RegisterEvent.LoadingComplete)

            val errorEvent = awaitItem()
            assertTrue(errorEvent is RegisterEvent.Error)
            assertEquals("All fields are required", (errorEvent as RegisterEvent.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 0) { registerUseCase.invoke(any(), any(), any(), any()) }
    }

    @Test
    fun `register should emit error event when email format is invalid`() = runTest {
        // Given
        val username = "johndoe"
        val fullName = "John Doe"
        val email = "invalidemail"
        val password = "password123"

        // When
        viewModel.events.test {
            viewModel.register(username, fullName, email, password)
            advanceUntilIdle()

            // Then
            val loadingEvent = awaitItem()
            assertTrue(loadingEvent is RegisterEvent.Loading)

            val loadingCompleteEvent = awaitItem()
            assertTrue(loadingCompleteEvent is RegisterEvent.LoadingComplete)

            val errorEvent = awaitItem()
            assertTrue(errorEvent is RegisterEvent.Error)
            assertEquals("Invalid email format", (errorEvent as RegisterEvent.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 0) { registerUseCase.invoke(any(), any(), any(), any()) }
    }

    @Test
    fun `register should emit error event when email is missing at symbol`() = runTest {
        // Given
        val username = "johndoe"
        val fullName = "John Doe"
        val email = "emailexample.com"
        val password = "password123"

        // When
        viewModel.events.test {
            viewModel.register(username, fullName, email, password)
            advanceUntilIdle()

            // Then
            val loadingEvent = awaitItem()
            assertTrue(loadingEvent is RegisterEvent.Loading)

            val loadingCompleteEvent = awaitItem()
            assertTrue(loadingCompleteEvent is RegisterEvent.LoadingComplete)

            val errorEvent = awaitItem()
            assertTrue(errorEvent is RegisterEvent.Error)
            assertEquals("Invalid email format", (errorEvent as RegisterEvent.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 0) { registerUseCase.invoke(any(), any(), any(), any()) }
    }

    @Test
    fun `register should emit error event when use case returns failure`() = runTest {
        // Given
        val username = "johndoe"
        val fullName = "John Doe"
        val email = "john@example.com"
        val password = "password123"
        val errorMessage = "Username already exists"
        coEvery { registerUseCase.invoke(username, fullName, email, password) } returns Result.failure(Exception(errorMessage))

        // When
        viewModel.events.test {
            viewModel.register(username, fullName, email, password)
            advanceUntilIdle()

            // Then
            val loadingEvent = awaitItem()
            assertTrue(loadingEvent is RegisterEvent.Loading)

            val loadingCompleteEvent = awaitItem()
            assertTrue(loadingCompleteEvent is RegisterEvent.LoadingComplete)

            val errorEvent = awaitItem()
            assertTrue(errorEvent is RegisterEvent.Error)
            assertEquals(errorMessage, (errorEvent as RegisterEvent.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
        coVerify { registerUseCase.invoke(username, fullName, email, password) }
    }

    @Test
    fun `register should emit error with default message when exception message is null`() = runTest {
        // Given
        val username = "johndoe"
        val fullName = "John Doe"
        val email = "john@example.com"
        val password = "password123"
        coEvery { registerUseCase.invoke(username, fullName, email, password) } returns Result.failure(Exception())

        // When
        viewModel.events.test {
            viewModel.register(username, fullName, email, password)
            advanceUntilIdle()

            // Then
            val loadingEvent = awaitItem()
            assertTrue(loadingEvent is RegisterEvent.Loading)

            val loadingCompleteEvent = awaitItem()
            assertTrue(loadingCompleteEvent is RegisterEvent.LoadingComplete)

            val errorEvent = awaitItem()
            assertTrue(errorEvent is RegisterEvent.Error)
            assertEquals("Unknown error", (errorEvent as RegisterEvent.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
        coVerify { registerUseCase.invoke(username, fullName, email, password) }
    }

    @Test
    fun `register should handle whitespace in fields as blank`() = runTest {
        // Given
        val username = "   "
        val fullName = "John Doe"
        val email = "john@example.com"
        val password = "password123"

        // When
        viewModel.events.test {
            viewModel.register(username, fullName, email, password)
            advanceUntilIdle()

            // Then
            val loadingEvent = awaitItem()
            assertTrue(loadingEvent is RegisterEvent.Loading)

            val loadingCompleteEvent = awaitItem()
            assertTrue(loadingCompleteEvent is RegisterEvent.LoadingComplete)

            val errorEvent = awaitItem()
            assertTrue(errorEvent is RegisterEvent.Error)
            assertEquals("All fields are required", (errorEvent as RegisterEvent.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 0) { registerUseCase.invoke(any(), any(), any(), any()) }
    }

    @Test
    fun `register should accept valid email formats`() = runTest {
        // Given
        val username = "johndoe"
        val fullName = "John Doe"
        val email = "john.doe@example.co.uk"
        val password = "password123"
        coEvery { registerUseCase.invoke(username, fullName, email, password) } returns Result.success("Registration successful")

        // When
        viewModel.events.test {
            viewModel.register(username, fullName, email, password)
            advanceUntilIdle()

            // Then
            val loadingEvent = awaitItem()
            assertTrue(loadingEvent is RegisterEvent.Loading)

            val loadingCompleteEvent = awaitItem()
            assertTrue(loadingCompleteEvent is RegisterEvent.LoadingComplete)

            val successEvent = awaitItem()
            assertTrue(successEvent is RegisterEvent.Success)

            cancelAndIgnoreRemainingEvents()
        }
        coVerify { registerUseCase.invoke(username, fullName, email, password) }
    }

    @Test
    fun `register should call use case with correct parameters`() = runTest {
        // Given
        val username = "testuser"
        val fullName = "Test User"
        val email = "test@test.com"
        val password = "testpass"
        coEvery { registerUseCase.invoke(username, fullName, email, password) } returns Result.success("Registration successful")

        // When
        viewModel.register(username, fullName, email, password)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { registerUseCase.invoke(username, fullName, email, password) }
    }
}
