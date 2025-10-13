package com.amary.poke.mobile.domain.usecase

import app.cash.turbine.test
import com.amary.poke.mobile.domain.model.DetailModel
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
class DetailUseCaseTest {

    private lateinit var repository: PokeRepository
    private lateinit var detailUseCase: DetailUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        detailUseCase = DetailUseCase(repository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke should emit success with DetailModel when repository returns data`() = runTest {
        // Given
        val pokemonName = "pikachu"
        val expectedDetail = DetailModel(
            abilities = emptyList(),
            baseExperience = 112,
            height = 4,
            id = 25,
            isDefault = true,
            locationAreaEncounters = "https://pokeapi.co/api/v2/pokemon/25/encounters",
            name = "pikachu",
            order = 35,
            weight = 60
        )
        coEvery { repository.getPokemonDetail(pokemonName) } returns expectedDetail

        // When
        val flow = detailUseCase(pokemonName)

        // Then
        flow.test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(expectedDetail, result.getOrNull())
            assertEquals("pikachu", result.getOrNull()?.name)
            assertEquals(25, result.getOrNull()?.id)
            assertEquals(112, result.getOrNull()?.baseExperience)
            awaitComplete()
        }
        coVerify { repository.getPokemonDetail(pokemonName) }
    }

    @Test
    fun `invoke should emit success with complete DetailModel data`() = runTest {
        // Given
        val pokemonName = "charizard"
        val expectedDetail = DetailModel(
            abilities = emptyList(),
            baseExperience = 267,
            height = 17,
            id = 6,
            isDefault = true,
            locationAreaEncounters = "https://pokeapi.co/api/v2/pokemon/6/encounters",
            name = "charizard",
            order = 7,
            weight = 905
        )
        coEvery { repository.getPokemonDetail(pokemonName) } returns expectedDetail

        // When
        val flow = detailUseCase(pokemonName)

        // Then
        flow.test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertNotNull(result.getOrNull())
            val detail = result.getOrNull()!!
            assertEquals("charizard", detail.name)
            assertEquals(6, detail.id)
            assertEquals(267, detail.baseExperience)
            assertEquals(17, detail.height)
            assertEquals(905, detail.weight)
            awaitComplete()
        }
        coVerify { repository.getPokemonDetail(pokemonName) }
    }

    @Test
    fun `invoke should emit failure when repository throws exception`() = runTest {
        // Given
        val pokemonName = "unknown"
        val expectedException = Exception("Pokemon not found")
        coEvery { repository.getPokemonDetail(pokemonName) } throws expectedException

        // When
        val flow = detailUseCase(pokemonName)

        // Then
        flow.test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertEquals(expectedException, result.exceptionOrNull())
            assertEquals("Pokemon not found", result.exceptionOrNull()?.message)
            awaitComplete()
        }
        coVerify { repository.getPokemonDetail(pokemonName) }
    }

    @Test
    fun `invoke should emit failure when repository throws RuntimeException`() = runTest {
        // Given
        val pokemonName = "error"
        val expectedException = RuntimeException("Network error")
        coEvery { repository.getPokemonDetail(pokemonName) } throws expectedException

        // When
        val flow = detailUseCase(pokemonName)

        // Then
        flow.test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertNotNull(result.exceptionOrNull())
            assertEquals("Network error", result.exceptionOrNull()?.message)
            assertTrue(result.exceptionOrNull() is RuntimeException)
            awaitComplete()
        }
        coVerify { repository.getPokemonDetail(pokemonName) }
    }

    @Test
    fun `invoke should emit failure with proper exception message`() = runTest {
        // Given
        val pokemonName = "bulbasaur"
        val errorMessage = "API service unavailable"
        val expectedException = Exception(errorMessage)
        coEvery { repository.getPokemonDetail(pokemonName) } throws expectedException

        // When
        val flow = detailUseCase(pokemonName)

        // Then
        flow.test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertEquals(errorMessage, result.exceptionOrNull()?.message)
            awaitComplete()
        }
        coVerify { repository.getPokemonDetail(pokemonName) }
    }

    @Test
    fun `invoke should call repository getPokemonDetail exactly once`() = runTest {
        // Given
        val pokemonName = "squirtle"
        val expectedDetail = DetailModel(
            id = 7,
            name = "squirtle",
            baseExperience = 63,
            height = 5,
            weight = 90
        )
        coEvery { repository.getPokemonDetail(pokemonName) } returns expectedDetail

        // When
        val flow = detailUseCase(pokemonName)

        // Then
        flow.test {
            awaitItem()
            awaitComplete()
        }
        coVerify(exactly = 1) { repository.getPokemonDetail(pokemonName) }
    }

    @Test
    fun `invoke should handle different pokemon names correctly`() = runTest {
        // Given
        val pokemon1 = "mewtwo"
        val pokemon2 = "mew"
        val detail1 = DetailModel(id = 150, name = "mewtwo", baseExperience = 306)
        val detail2 = DetailModel(id = 151, name = "mew", baseExperience = 300)
        coEvery { repository.getPokemonDetail(pokemon1) } returns detail1
        coEvery { repository.getPokemonDetail(pokemon2) } returns detail2

        // When
        val flow1 = detailUseCase(pokemon1)
        val flow2 = detailUseCase(pokemon2)

        // Then
        flow1.test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals("mewtwo", result.getOrNull()?.name)
            assertEquals(150, result.getOrNull()?.id)
            awaitComplete()
        }

        flow2.test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals("mew", result.getOrNull()?.name)
            assertEquals(151, result.getOrNull()?.id)
            awaitComplete()
        }

        coVerify(exactly = 1) { repository.getPokemonDetail(pokemon1) }
        coVerify(exactly = 1) { repository.getPokemonDetail(pokemon2) }
    }

    @Test
    fun `invoke should emit DetailModel with default values when repository returns minimal data`() = runTest {
        // Given
        val pokemonName = "ditto"
        val expectedDetail = DetailModel(name = "ditto")
        coEvery { repository.getPokemonDetail(pokemonName) } returns expectedDetail

        // When
        val flow = detailUseCase(pokemonName)

        // Then
        flow.test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            val detail = result.getOrNull()!!
            assertEquals("ditto", detail.name)
            assertEquals(0, detail.id)
            assertEquals(0, detail.baseExperience)
            assertEquals(0, detail.height)
            assertEquals(0, detail.weight)
            assertEquals(emptyList<Any>(), detail.abilities)
            awaitComplete()
        }
        coVerify { repository.getPokemonDetail(pokemonName) }
    }

    @Test
    fun `invoke should execute on the provided dispatcher`() = runTest {
        // Given
        val pokemonName = "eevee"
        val expectedDetail = DetailModel(id = 133, name = "eevee", baseExperience = 65)
        coEvery { repository.getPokemonDetail(pokemonName) } returns expectedDetail

        // When
        val flow = detailUseCase(pokemonName)

        // Then
        flow.test {
            testDispatcher.scheduler.advanceUntilIdle()
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals("eevee", result.getOrNull()?.name)
            awaitComplete()
        }
        coVerify { repository.getPokemonDetail(pokemonName) }
    }

    @Test
    fun `invoke should complete flow after emitting single item`() = runTest {
        // Given
        val pokemonName = "snorlax"
        val expectedDetail = DetailModel(id = 143, name = "snorlax")
        coEvery { repository.getPokemonDetail(pokemonName) } returns expectedDetail

        // When
        val flow = detailUseCase(pokemonName)

        // Then
        flow.test {
            assertNotNull(awaitItem())
            awaitComplete()
        }
        coVerify { repository.getPokemonDetail(pokemonName) }
    }

    @Test
    fun `invoke should handle IOException from repository`() = runTest {
        // Given
        val pokemonName = "articuno"
        val expectedException = java.io.IOException("Connection timeout")
        coEvery { repository.getPokemonDetail(pokemonName) } throws expectedException

        // When
        val flow = detailUseCase(pokemonName)

        // Then
        flow.test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is java.io.IOException)
            assertEquals("Connection timeout", result.exceptionOrNull()?.message)
            awaitComplete()
        }
        coVerify { repository.getPokemonDetail(pokemonName) }
    }

    @Test
    fun `invoke should not emit multiple items`() = runTest {
        // Given
        val pokemonName = "dragonite"
        val expectedDetail = DetailModel(id = 149, name = "dragonite", baseExperience = 300)
        coEvery { repository.getPokemonDetail(pokemonName) } returns expectedDetail

        // When
        val flow = detailUseCase(pokemonName)

        // Then
        flow.test {
            awaitItem() // First and only item
            awaitComplete() // Should complete immediately
        }
        coVerify { repository.getPokemonDetail(pokemonName) }
    }

    @Test
    fun `invoke should handle empty string pokemon name`() = runTest {
        // Given
        val pokemonName = ""
        val expectedException = IllegalArgumentException("Pokemon name cannot be empty")
        coEvery { repository.getPokemonDetail(pokemonName) } throws expectedException

        // When
        val flow = detailUseCase(pokemonName)

        // Then
        flow.test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is IllegalArgumentException)
            awaitComplete()
        }
        coVerify { repository.getPokemonDetail(pokemonName) }
    }
}
