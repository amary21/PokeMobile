package com.amary.poke.mobile.domain.usecase

import app.cash.turbine.test
import com.amary.poke.mobile.domain.model.PokeModel
import com.amary.poke.mobile.domain.model.ResultModel
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
class ListUseCaseTest {

    private lateinit var repository: PokeRepository
    private lateinit var listUseCase: ListUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        listUseCase = ListUseCase(repository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke should emit success with pokemon list when repository returns data`() = runTest {
        // Given
        val offset = 10
        val remotePokeModel = PokeModel(
            next = 20,
            result = listOf(
                ResultModel(name = "pikachu", url = "url1"),
                ResultModel(name = "bulbasaur", url = "url2")
            )
        )
        val localPokemon = listOf(
            ResultModel(name = "pikachu", url = "url1"),
            ResultModel(name = "bulbasaur", url = "url2")
        )
        coEvery { repository.listPokemon(10, offset) } returns remotePokeModel
        coEvery { repository.deletePokemon() } returns Unit
        coEvery { repository.savePokemon(any()) } returns Unit
        coEvery { repository.listLocalPokemon() } returns localPokemon

        // When
        val flow = listUseCase(offset)

        // Then
        flow.test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(20, result.getOrNull()?.next)
            assertEquals(2, result.getOrNull()?.result?.size)
            assertEquals("pikachu", result.getOrNull()?.result?.get(0)?.name)
            awaitComplete()
        }
        coVerify { repository.listPokemon(10, offset) }
        coVerify { repository.deletePokemon() }
        coVerify { repository.savePokemon(any()) }
        coVerify { repository.listLocalPokemon() }
    }

    @Test
    fun `invoke should delete pokemon when offset is 10`() = runTest {
        // Given
        val offset = 10
        val remotePokeModel = PokeModel(next = 20, result = emptyList())
        coEvery { repository.listPokemon(10, offset) } returns remotePokeModel
        coEvery { repository.deletePokemon() } returns Unit
        coEvery { repository.savePokemon(any()) } returns Unit
        coEvery { repository.listLocalPokemon() } returns emptyList()

        // When
        val flow = listUseCase(offset)

        // Then
        flow.test {
            awaitItem()
            awaitComplete()
        }
        coVerify(exactly = 1) { repository.deletePokemon() }
    }

    @Test
    fun `invoke should not delete pokemon when offset is not 10`() = runTest {
        // Given
        val offset = 20
        val remotePokeModel = PokeModel(next = 30, result = emptyList())
        coEvery { repository.listPokemon(10, offset) } returns remotePokeModel
        coEvery { repository.savePokemon(any()) } returns Unit
        coEvery { repository.listLocalPokemon() } returns emptyList()

        // When
        val flow = listUseCase(offset)

        // Then
        flow.test {
            awaitItem()
            awaitComplete()
        }
        coVerify(exactly = 0) { repository.deletePokemon() }
    }

    @Test
    fun `invoke should remove duplicate pokemon by name`() = runTest {
        // Given
        val offset = 0
        val remotePokeModel = PokeModel(
            next = 10,
            result = listOf(ResultModel(name = "pikachu", url = "url1"))
        )
        val localPokemonWithDuplicates = listOf(
            ResultModel(name = "pikachu", url = "url1"),
            ResultModel(name = "pikachu", url = "url2"),
            ResultModel(name = "bulbasaur", url = "url3")
        )
        coEvery { repository.listPokemon(10, offset) } returns remotePokeModel
        coEvery { repository.savePokemon(any()) } returns Unit
        coEvery { repository.listLocalPokemon() } returns localPokemonWithDuplicates

        // When
        val flow = listUseCase(offset)

        // Then
        flow.test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(2, result.getOrNull()?.result?.size)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should emit failure when repository throws exception and local is empty`() = runTest {
        // Given
        val offset = 0
        val expectedException = Exception("Network error")
        coEvery { repository.listPokemon(10, offset) } throws expectedException
        coEvery { repository.listLocalPokemon() } returns emptyList()

        // When
        val flow = listUseCase(offset)

        // Then
        flow.test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertNotNull(result.exceptionOrNull())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should emit success with local data when repository throws exception but local has data`() = runTest {
        // Given
        val offset = 0
        val expectedException = Exception("Network error")
        val localPokemon = listOf(
            ResultModel(name = "charmander", url = "url1"),
            ResultModel(name = "charmeleon", url = "url2")
        )
        coEvery { repository.listPokemon(10, offset) } throws expectedException
        coEvery { repository.listLocalPokemon() } returns localPokemon

        // When
        val flow = listUseCase(offset)

        // Then
        flow.test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(2, result.getOrNull()?.result?.size)
            assertEquals(offset, result.getOrNull()?.next)
            assertEquals("charmander", result.getOrNull()?.result?.get(0)?.name)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should save pokemon to local database`() = runTest {
        // Given
        val offset = 0
        val pokemonList = listOf(
            ResultModel(name = "squirtle", url = "url1"),
            ResultModel(name = "wartortle", url = "url2")
        )
        val remotePokeModel = PokeModel(next = 10, result = pokemonList)
        coEvery { repository.listPokemon(10, offset) } returns remotePokeModel
        coEvery { repository.savePokemon(any()) } returns Unit
        coEvery { repository.listLocalPokemon() } returns pokemonList

        // When
        val flow = listUseCase(offset)

        // Then
        flow.test {
            awaitItem()
            awaitComplete()
        }
        coVerify { repository.savePokemon(pokemonList) }
    }

    @Test
    fun `invoke should execute on the provided dispatcher`() = runTest {
        // Given
        val offset = 0
        val remotePokeModel = PokeModel(next = 10, result = emptyList())
        coEvery { repository.listPokemon(10, offset) } returns remotePokeModel
        coEvery { repository.savePokemon(any()) } returns Unit
        coEvery { repository.listLocalPokemon() } returns emptyList()

        // When
        val flow = listUseCase(offset)

        // Then
        flow.test {
            testDispatcher.scheduler.advanceUntilIdle()
            val result = awaitItem()
            assertTrue(result.isSuccess)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should handle multiple consecutive calls correctly`() = runTest {
        // Given
        val offset1 = 0
        val offset2 = 10
        val remotePokeModel1 = PokeModel(next = 10, result = listOf(ResultModel(name = "pokemon1", url = "url1")))
        val remotePokeModel2 = PokeModel(next = 20, result = listOf(ResultModel(name = "pokemon2", url = "url2")))
        coEvery { repository.listPokemon(10, offset1) } returns remotePokeModel1
        coEvery { repository.listPokemon(10, offset2) } returns remotePokeModel2
        coEvery { repository.savePokemon(any()) } returns Unit
        coEvery { repository.deletePokemon() } returns Unit
        coEvery { repository.listLocalPokemon() } returns listOf(ResultModel(name = "pokemon1", url = "url1")) andThen listOf(ResultModel(name = "pokemon2", url = "url2"))

        // When
        val flow1 = listUseCase(offset1)
        val flow2 = listUseCase(offset2)

        // Then
        flow1.test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(10, result.getOrNull()?.next)
            awaitComplete()
        }

        flow2.test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(20, result.getOrNull()?.next)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return empty list when no pokemon available`() = runTest {
        // Given
        val offset = 0
        val remotePokeModel = PokeModel(next = 10, result = emptyList())
        coEvery { repository.listPokemon(10, offset) } returns remotePokeModel
        coEvery { repository.savePokemon(any()) } returns Unit
        coEvery { repository.listLocalPokemon() } returns emptyList()

        // When
        val flow = listUseCase(offset)

        // Then
        flow.test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(0, result.getOrNull()?.result?.size)
            awaitComplete()
        }
    }
}

