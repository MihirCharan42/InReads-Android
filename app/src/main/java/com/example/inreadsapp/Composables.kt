package com.example.inreadsapp

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.inreadsapp.models.Quote
import com.example.inreadsapp.utils.NetworkResult
import com.example.inreadsapp.viewmodel.QuotesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun QuotesList() {
    val page = remember { mutableStateOf(1) }
    var currentIndex = 0
    var scope = rememberCoroutineScope()
    val loading = remember { mutableStateOf(false) }
    val itemList = remember { mutableStateListOf<Quote>() }
    val listState = rememberLazyListState()
    var job: Job? = null

    val quotesViewModel: QuotesViewModel = hiltViewModel()

    val quotesList = quotesViewModel.quotesStateFlow.collectAsState()

    LaunchedEffect(key1 = page.value) {
        quotesViewModel.getQuotes(page.value, emptyList())
    }

    LaunchedEffect(key1 = quotesList.value, block = {
        if(quotesList.value.data != null){
            itemList.addAll(quotesList.value.data!!.results)
        }
    })

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collectLatest { index ->
                if (!loading.value && index != null && index >= itemList.size - 5) {
                    page.value++
                }
            }
    }

    when(quotesList.value) {
        is NetworkResult.Success -> {
            LazyColumn(
                state = listState,
                userScrollEnabled = false,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val (x, y) = dragAmount
                            job?.cancel()
                            job = scope.launch {
                                delay(100)
                                when {
                                    y > 0 -> { /* swiped down */
                                        try {
                                            if(currentIndex != 0) {
                                                listState.animateScrollToItem(--currentIndex)
                                            }
                                        } catch (e: Exception) { e.printStackTrace()
                                        }
                                    }

                                    y < 0 -> { /* swiped up */
                                        try {
                                            listState.animateScrollToItem(++currentIndex)
                                        } catch (e: Exception) { e.printStackTrace()
                                        }
                                    }
                                }
                            }
                        }
                    },
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(itemList) { item ->
                    Card(
                        modifier = Modifier
                            .fillParentMaxSize()
                            .padding(16.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = item.content, modifier = Modifier.padding(16.dp))
                        }
                    }
                }
                item {
                    Card(
                        modifier = Modifier
                            .fillParentMaxSize()
                            .padding(16.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(50.dp), strokeWidth = 2.dp)

                        }
                    }
                }
            }
        }
        is NetworkResult.Loading -> {
            if(page.value==1){
                Box(modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(50.dp), strokeWidth = 2.dp)
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(itemList) { item ->
                        Card(
                            modifier = Modifier
                                .fillParentMaxSize()
                                .padding(16.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = item.content, modifier = Modifier.padding(16.dp))
                            }
                        }
                    }

                    item {
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(50.dp), strokeWidth = 2.dp)
                            }
                    }
                }
            }
        }
        is NetworkResult.Error -> {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Something went wrong, please retry!", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}