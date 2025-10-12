package com.amary.poke.mobile.presentation.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.amary.poke.mobile.domain.model.ResultModel
import com.amary.poke.mobile.presentation.component.ProgressDialog

@Composable
fun ListScreen(
    listState: ListState,
    searchState: SearchState,
    onGetListItem: (offset: Int) -> Unit = {},
    onGetSearch: (name: String) -> Unit = {},
    onItemClick: (ResultModel) -> Unit = {},
    onNavigateToDetail: (name: String) -> Unit = {},
) {

    LaunchedEffect(key1 = Unit) {
        onGetListItem(10)
    }

    Scaffold(
        topBar = {
            SearchComponent(
                searchState = searchState,
                onGetSearch = onGetSearch,
                onNavigateToDetail = onNavigateToDetail
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (listState) {
                is ListState.Loading -> {
                    ProgressDialog(
                        isShowing = true,
                        message = "Loading Pokémon..."
                    )
                }

                is ListState.Success -> {
                    PokemonList(
                        items = listState.list,
                        offset = listState.offset,
                        onGetListItem = onGetListItem,
                        onItemClick = onItemClick
                    )
                }

                is ListState.Error -> {
                    Text(
                        text = listState.message,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun PokemonList(
    items: List<ResultModel>,
    offset: Int,
    onGetListItem: (Int) -> Unit,
    onItemClick: (ResultModel) -> Unit,
) {
    val lastIndex = items.lastIndex
    LazyColumn {
        item {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "List of Pokémon",
                style = MaterialTheme.typography.titleMedium,
            )
        }
        itemsIndexed(items) { index, item ->
            PokemonItem(item = item, onItemClick = onItemClick)

            if (index == lastIndex && offset > 10) {
                onGetListItem(offset)
            }
        }
        items(items) { item ->
            PokemonItem(
                item = item,
                onItemClick = onItemClick
            )
        }
    }
}

@Composable
fun PokemonItem(
    item: ResultModel,
    onItemClick: (ResultModel) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onItemClick(item) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = item.name,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview
@Composable
fun ListScreenPreview() {
    ListScreen(
        listState = ListState.Success(
            listOf(
                ResultModel(
                    name = "Bulbasaur",
                    url = "https://pokeapi.co/api/v2/pokemon/1/"
                ),
                ResultModel(
                    name = "Charmander",
                    url = "https://pokeapi.co/api/v2/pokemon/4/"
                )
            )
        ),
        searchState = SearchState.Initial,
    )
}
