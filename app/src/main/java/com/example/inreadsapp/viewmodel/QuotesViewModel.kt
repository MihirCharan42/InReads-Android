package com.example.inreadsapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.inreadsapp.api.QuotesApi
import com.example.inreadsapp.models.Quote
import com.example.inreadsapp.models.QuotesList
import com.example.inreadsapp.repo.QuotesRepository
import com.example.inreadsapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuotesViewModel @Inject constructor(private val quotesRepository: QuotesRepository): ViewModel() {

    val quotesStateFlow: StateFlow<NetworkResult<QuotesList>>
        get() = quotesRepository.quoteStateFlow

    fun getQuotes(page: Int, list: List<Quote>) {
        viewModelScope.launch {
            quotesRepository.getQuotes(page, list)
        }
    }
}