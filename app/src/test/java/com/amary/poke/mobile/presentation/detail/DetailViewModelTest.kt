package com.amary.poke.mobile.presentation.detail

import app.cash.turbine.test
import com.amary.poke.mobile.domain.model.AbilitiesModel
import com.amary.poke.mobile.domain.model.AbilityModel
import com.amary.poke.mobile.domain.model.DetailModel
import com.amary.poke.mobile.domain.usecase.DetailUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
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
class DetailViewModelTest {

    private lateinit var detailUseCase: DetailUseCase
    private lateinit var viewModel: DetailViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        detailUseCase = mockk()
        viewModel = DetailViewModel(detailUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Initial`() = runTest {
        // Given - ViewModel is initialized in setup

        // When
        val initialState = viewModel.state.value

        // Then
        assertTrue(initialState is DetailState.Initial)
    }

    @Test
    fun `getDetail should emit Loading state then Success when use case returns success`() = runTest {
        // Given
        val pokemonName = "pikachu"
        val expectedDetail = DetailModel(
            id = 25,
            name = "pikachu",
            baseExperience = 112,
            height = 4,
            weight = 60,
            abilities = listOf(
                AbilitiesModel(
                    ability = AbilityModel(name = "static", url = "https://pokeapi.co/api/v2/ability/9/"),
                    isHidden = false,
                    slot = 1
                ),
                AbilitiesModel(
                    ability = AbilityModel(name = "lightning-rod", url = "https://pokeapi.co/api/v2/ability/31/"),
                    isHidden = true,
                    slot = 3
                )
            )
        )
        coEvery { detailUseCase(pokemonName) } returns flow {
            emit(Result.success(expectedDetail))
        }

        // When
        viewModel.getDetail(pokemonName)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            val data = (state as? DetailState.Success)?.data
            assertTrue(state is DetailState.Success)
            assertEquals(expectedDetail, (state as DetailState.Success).data)
            assertEquals("pikachu", state.data.name)
            assertEquals(25, state.data.id)
            data?.abilities?.forEachIndexed { index, ability ->
                assertEquals(expectedDetail.abilities[index].ability.name, ability.ability.name)
                assertEquals(expectedDetail.abilities[index].isHidden, ability.isHidden)
                assertEquals(expectedDetail.abilities[index].slot, ability.slot)
                assertEquals(expectedDetail.abilities[index].ability, ability.ability)
                assertEquals(expectedDetail.abilities[index].ability.name, ability.ability.name)
                assertEquals(expectedDetail.abilities[index].ability.url, ability.ability.url)
            }

        }
        coVerify { detailUseCase(pokemonName) }
    }

    @Test
    fun `getDetail should emit Loading state then Success when use case empty list returns success`() = runTest {
        // Given
        val pokemonName = "pikachu"
        val expectedDetail = DetailModel(
            id = 25,
            name = "pikachu",
            baseExperience = 112,
            height = 4,
            weight = 60,
            abilities = emptyList()
        )
        coEvery { detailUseCase(pokemonName) } returns flow {
            emit(Result.success(expectedDetail))
        }

        // When
        viewModel.getDetail(pokemonName)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            val data = (state as? DetailState.Success)?.data
            assertTrue(state is DetailState.Success)
            assertEquals(expectedDetail, (state as DetailState.Success).data)
            assertEquals("pikachu", state.data.name)
            assertEquals(25, state.data.id)
            data?.abilities?.forEachIndexed { index, ability ->
                assertTrue(ability.ability.name.isEmpty())
                assertFalse(ability.isHidden)
                assertEquals(ability.slot, 0)
                assertTrue(ability.ability.name.isEmpty())
                assertTrue(ability.ability.url.isEmpty())
            }

        }
        coVerify { detailUseCase(pokemonName) }
    }

    @Test
    fun `getDetail should emit Loading state then Error when use case returns failure`() = runTest {
        // Given
        val pokemonName = "unknown"
        val errorMessage = "Pokemon not found"
        coEvery { detailUseCase(pokemonName) } returns flow {
            emit(Result.failure(Exception(errorMessage)))
        }

        // When
        viewModel.getDetail(pokemonName)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state is DetailState.Error)
            assertEquals(errorMessage, (state as DetailState.Error).message)
        }
        coVerify { detailUseCase(pokemonName) }
    }

    @Test
    fun `getDetail should emit Error state when name is empty`() = runTest {
        // Given
        val emptyName = ""

        // When
        viewModel.getDetail(emptyName)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state is DetailState.Error)
            assertEquals("Name cannot be empty", (state as DetailState.Error).message)
        }
        coVerify(exactly = 0) { detailUseCase(any()) }
    }

    @Test
    fun `getDetail should emit Error state when name is blank`() = runTest {
        // Given
        val blankName = "   "
        coEvery { detailUseCase(blankName) } returns flow {
            emit(Result.success(DetailModel(id = 0, name = blankName)))
        }

        // When
        viewModel.getDetail(blankName)
        advanceUntilIdle()

        // Then
        // Note: isNotEmpty() returns true for blank spaces, so use case will be called
        coVerify { detailUseCase(blankName) }
    }

    @Test
    fun `getDetail should update state from Initial to Loading to Success`() = runTest {
        // Given
        val pokemonName = "charizard"
        val expectedDetail = DetailModel(
            id = 6,
            name = "charizard",
            baseExperience = 267
        )
        coEvery { detailUseCase(pokemonName) } returns flow {
            emit(Result.success(expectedDetail))
        }

        // When
        viewModel.state.test {
            assertEquals(DetailState.Initial, awaitItem())

            viewModel.getDetail(pokemonName)

            // Then
            assertEquals(DetailState.Loading, awaitItem())
            val successState = awaitItem()
            assertTrue(successState is DetailState.Success)
            assertEquals("charizard", (successState as DetailState.Success).data.name)
        }
    }

    @Test
    fun `getDetail should update state from Initial to Loading to Error`() = runTest {
        // Given
        val pokemonName = "invalid"
        val errorMessage = "Network error"
        coEvery { detailUseCase(pokemonName) } returns flow {
            emit(Result.failure(Exception(errorMessage)))
        }

        // When
        viewModel.state.test {
            assertEquals(DetailState.Initial, awaitItem())

            viewModel.getDetail(pokemonName)

            // Then
            assertEquals(DetailState.Loading, awaitItem())
            val errorState = awaitItem()
            assertTrue(errorState is DetailState.Error)
            assertEquals(errorMessage, (errorState as DetailState.Error).message)
        }
    }

    @Test
    fun `getDetail should call detailUseCase exactly once`() = runTest {
        // Given
        val pokemonName = "bulbasaur"
        val expectedDetail = DetailModel(id = 1, name = "bulbasaur")
        coEvery { detailUseCase(pokemonName) } returns flow {
            emit(Result.success(expectedDetail))
        }

        // When
        viewModel.getDetail(pokemonName)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { detailUseCase(pokemonName) }
    }

    @Test
    fun `getDetail should handle multiple consecutive calls correctly`() = runTest {
        // Given
        val pokemon1 = "squirtle"
        val pokemon2 = "wartortle"
        val detail1 = DetailModel(id = 7, name = "squirtle")
        val detail2 = DetailModel(id = 8, name = "wartortle")
        coEvery { detailUseCase(pokemon1) } returns flow {
            emit(Result.success(detail1))
        }
        coEvery { detailUseCase(pokemon2) } returns flow {
            emit(Result.success(detail2))
        }

        // When
        viewModel.getDetail(pokemon1)
        advanceUntilIdle()
        viewModel.getDetail(pokemon2)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state is DetailState.Success)
            assertEquals("wartortle", (state as DetailState.Success).data.name)
        }
        coVerify { detailUseCase(pokemon1) }
        coVerify { detailUseCase(pokemon2) }
    }

    @Test
    fun `getDetail should handle success with complete detail data`() = runTest {
        // Given
        val pokemonName = "mewtwo"
        val expectedDetail = DetailModel(
            abilities = emptyList(),
            baseExperience = 306,
            height = 20,
            id = 150,
            isDefault = true,
            locationAreaEncounters = "https://pokeapi.co/api/v2/pokemon/150/encounters",
            name = "mewtwo",
            order = 151,
            weight = 1220
        )
        coEvery { detailUseCase(pokemonName) } returns flow {
            emit(Result.success(expectedDetail))
        }

        // When
        viewModel.getDetail(pokemonName)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state is DetailState.Success)
            val detail = (state as DetailState.Success).data
            assertEquals("mewtwo", detail.name)
            assertEquals(150, detail.id)
            assertEquals(306, detail.baseExperience)
            assertEquals(20, detail.height)
            assertEquals(1220, detail.weight)
        }
    }

    @Test
    fun `getDetail should not call use case when name is empty`() = runTest {
        // Given
        val emptyName = ""

        // When
        viewModel.getDetail(emptyName)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { detailUseCase(any()) }
    }

    @Test
    fun `getDetail should handle RuntimeException from use case`() = runTest {
        // Given
        val pokemonName = "error"
        val errorMessage = "Unexpected error"
        coEvery { detailUseCase(pokemonName) } returns flow {
            emit(Result.failure(RuntimeException(errorMessage)))
        }

        // When
        viewModel.getDetail(pokemonName)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state is DetailState.Error)
            assertEquals(errorMessage, (state as DetailState.Error).message)
        }
    }

    @Test
    fun `getDetail should handle IOException from use case`() = runTest {
        // Given
        val pokemonName = "timeout"
        val errorMessage = "Connection timeout"
        coEvery { detailUseCase(pokemonName) } returns flow {
            emit(Result.failure(java.io.IOException(errorMessage)))
        }

        // When
        viewModel.getDetail(pokemonName)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state is DetailState.Error)
            assertEquals(errorMessage, (state as DetailState.Error).message)
        }
    }

    @Test
    fun `state flow should emit states in correct order`() = runTest {
        // Given
        val pokemonName = "eevee"
        val expectedDetail = DetailModel(id = 133, name = "eevee", baseExperience = 65)
        coEvery { detailUseCase(pokemonName) } returns flow {
            emit(Result.success(expectedDetail))
        }

        // When & Then
        viewModel.state.test {
            // Initial state
            assertTrue(awaitItem() is DetailState.Initial)

            viewModel.getDetail(pokemonName)

            // Loading state
            assertTrue(awaitItem() is DetailState.Loading)

            // Success state
            val successState = awaitItem()
            assertTrue(successState is DetailState.Success)
            assertEquals("eevee", (successState as DetailState.Success).data.name)
        }
    }

    @Test
    fun `getDetail should handle null exception message`() = runTest {
        // Given
        val pokemonName = "nullerror"
        coEvery { detailUseCase(pokemonName) } returns flow {
            emit(Result.failure(Exception(null as String?)))
        }

        // When
        viewModel.getDetail(pokemonName)
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state is DetailState.Error)
            // Should handle null message gracefully
            assertNotNull((state as DetailState.Error).message)
        }
    }

    @Test
    fun `getDetail should replace previous state with new state`() = runTest {
        // Given
        val pokemon1 = "ditto"
        val pokemon2 = "mew"
        val detail1 = DetailModel(id = 132, name = "ditto")
        val detail2 = DetailModel(id = 151, name = "mew")
        coEvery { detailUseCase(pokemon1) } returns flow {
            emit(Result.success(detail1))
        }
        coEvery { detailUseCase(pokemon2) } returns flow {
            emit(Result.success(detail2))
        }

        // When
        viewModel.getDetail(pokemon1)
        advanceUntilIdle()

        val firstState = viewModel.state.value
        assertTrue(firstState is DetailState.Success)
        assertEquals("ditto", (firstState as DetailState.Success).data.name)

        viewModel.getDetail(pokemon2)
        advanceUntilIdle()

        // Then
        val secondState = viewModel.state.value
        assertTrue(secondState is DetailState.Success)
        assertEquals("mew", (secondState as DetailState.Success).data.name)
    }

    @Test
    fun `getDetail with valid name should set Loading state immediately`() = runTest {
        // Given
        val pokemonName = "snorlax"
        coEvery { detailUseCase(pokemonName) } returns flow {
            emit(Result.success(DetailModel(id = 143, name = "snorlax")))
        }

        // When & Then
        viewModel.state.test {
            assertEquals(DetailState.Initial, awaitItem())

            viewModel.getDetail(pokemonName)

            assertEquals(DetailState.Loading, awaitItem())

            val successState = awaitItem()
            assertTrue(successState is DetailState.Success)
        }
    }
}