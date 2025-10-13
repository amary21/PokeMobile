package com.amary.poke.mobile.presentation.profile

import app.cash.turbine.test
import com.amary.poke.mobile.domain.model.UserModel
import com.amary.poke.mobile.domain.usecase.LogoutUseCase
import com.amary.poke.mobile.domain.usecase.ProfileUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
class ProfileViewModelTest {

    private lateinit var profileUseCase: ProfileUseCase
    private lateinit var logoutUseCase: LogoutUseCase
    private lateinit var viewModel: ProfileViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        profileUseCase = mockk()
        logoutUseCase = mockk()
        viewModel = ProfileViewModel(profileUseCase, logoutUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getProfile should emit loading then success state when use case returns success`() = runTest {
        // Given
        val mockUser = UserModel(
            id = "1",
            userName = "johndoe",
            fullName = "John Doe",
            email = "john@example.com",
            password = "password"
        )
        coEvery { profileUseCase.invoke() } returns flowOf(Result.success(mockUser))

        // When
        viewModel.state.test {
            val initialState = awaitItem()
            assertTrue(initialState is ProfileState.Initial)

            viewModel.getProfile()
            advanceUntilIdle()

            // Then
            val loadingState = awaitItem()
            assertTrue(loadingState is ProfileState.Loading)

            val successState = awaitItem()
            assertTrue(successState is ProfileState.Success)
            assertEquals(mockUser, (successState as ProfileState.Success).user)

            cancelAndIgnoreRemainingEvents()
        }
        coVerify { profileUseCase.invoke() }
    }

    @Test
    fun `getProfile should emit loading then error state when use case returns failure`() = runTest {
        // Given
        val errorMessage = "User not found"
        coEvery { profileUseCase.invoke() } returns flowOf(Result.failure(Exception(errorMessage)))

        // When
        viewModel.state.test {
            val initialState = awaitItem()
            assertTrue(initialState is ProfileState.Initial)

            viewModel.getProfile()
            advanceUntilIdle()

            // Then
            val loadingState = awaitItem()
            assertTrue(loadingState is ProfileState.Loading)

            val errorState = awaitItem()
            assertTrue(errorState is ProfileState.Error)
            assertEquals(errorMessage, (errorState as ProfileState.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
        coVerify { profileUseCase.invoke() }
    }

    @Test
    fun `getProfile should emit error with default message when exception message is null`() = runTest {
        // Given
        coEvery { profileUseCase.invoke() } returns flowOf(Result.failure(Exception()))

        // When
        viewModel.state.test {
            val initialState = awaitItem()
            assertTrue(initialState is ProfileState.Initial)

            viewModel.getProfile()
            advanceUntilIdle()

            // Then
            val loadingState = awaitItem()
            assertTrue(loadingState is ProfileState.Loading)

            val errorState = awaitItem()
            assertTrue(errorState is ProfileState.Error)
            assertEquals("Unknown error", (errorState as ProfileState.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
        coVerify { profileUseCase.invoke() }
    }

    @Test
    fun `logout should emit loading, loadingComplete and success events when logout succeeds`() = runTest {
        // Given
        coEvery { logoutUseCase.invoke() } returns Result.success("Logout successful")

        // When
        viewModel.events.test {
            viewModel.logout()
            advanceUntilIdle()

            // Then
            val loadingEvent = awaitItem()
            assertTrue(loadingEvent is ProfileEvent.Loading)

            val loadingCompleteEvent = awaitItem()
            assertTrue(loadingCompleteEvent is ProfileEvent.LoadingComplete)

            val successEvent = awaitItem()
            assertTrue(successEvent is ProfileEvent.Success)

            cancelAndIgnoreRemainingEvents()
        }
        coVerify { logoutUseCase.invoke() }
    }

    @Test
    fun `logout should emit loading, loadingComplete and error events when logout fails`() = runTest {
        // Given
        val errorMessage = "Logout failed due to network error"
        coEvery { logoutUseCase.invoke() } returns Result.failure(Exception(errorMessage))

        // When
        viewModel.events.test {
            viewModel.logout()
            advanceUntilIdle()

            // Then
            val loadingEvent = awaitItem()
            assertTrue(loadingEvent is ProfileEvent.Loading)

            val loadingCompleteEvent = awaitItem()
            assertTrue(loadingCompleteEvent is ProfileEvent.LoadingComplete)

            val errorEvent = awaitItem()
            assertTrue(errorEvent is ProfileEvent.Error)
            assertEquals(errorMessage, (errorEvent as ProfileEvent.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
        coVerify { logoutUseCase.invoke() }
    }

    @Test
    fun `logout should emit error with default message when exception message is null`() = runTest {
        // Given
        coEvery { logoutUseCase.invoke() } returns Result.failure(Exception())

        // When
        viewModel.events.test {
            viewModel.logout()
            advanceUntilIdle()

            // Then
            val loadingEvent = awaitItem()
            assertTrue(loadingEvent is ProfileEvent.Loading)

            val loadingCompleteEvent = awaitItem()
            assertTrue(loadingCompleteEvent is ProfileEvent.LoadingComplete)

            val errorEvent = awaitItem()
            assertTrue(errorEvent is ProfileEvent.Error)
            assertEquals("Logout failed", (errorEvent as ProfileEvent.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
        coVerify { logoutUseCase.invoke() }
    }

    @Test
    fun `getProfile should call profileUseCase exactly once`() = runTest {
        // Given
        val mockUser = UserModel(id = "1", userName = "test", fullName = "Test", email = "test@test.com")
        coEvery { profileUseCase.invoke() } returns flowOf(Result.success(mockUser))

        // When
        viewModel.getProfile()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { profileUseCase.invoke() }
    }

    @Test
    fun `logout should call logoutUseCase exactly once`() = runTest {
        // Given
        coEvery { logoutUseCase.invoke() } returns Result.success("Logout successful")

        // When
        viewModel.logout()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { logoutUseCase.invoke() }
    }

    @Test
    fun `getProfile should handle multiple user data fields correctly`() = runTest {
        // Given
        val mockUser = UserModel(
            id = "12345",
            userName = "testuser",
            fullName = "Test User Name",
            email = "test.user@domain.com",
            password = "encrypted"
        )
        coEvery { profileUseCase.invoke() } returns flowOf(Result.success(mockUser))

        // When
        viewModel.state.test {
            val initialState = awaitItem()
            assertTrue(initialState is ProfileState.Initial)

            viewModel.getProfile()
            advanceUntilIdle()

            // Then
            val loadingState = awaitItem()
            assertTrue(loadingState is ProfileState.Loading)

            val successState = awaitItem()
            assertTrue(successState is ProfileState.Success)
            val user = (successState as ProfileState.Success).user
            assertEquals("12345", user.id)
            assertEquals("testuser", user.userName)
            assertEquals("Test User Name", user.fullName)
            assertEquals("test.user@domain.com", user.email)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
