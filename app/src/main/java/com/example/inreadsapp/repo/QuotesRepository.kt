package com.example.inreadsapp.repo

import android.util.Log
import com.example.inreadsapp.api.QuotesApi
import com.example.inreadsapp.models.Quote
import com.example.inreadsapp.models.QuotesList
import com.example.inreadsapp.utils.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.lang.Exception
import javax.inject.Inject

class QuotesRepository @Inject constructor(private val quotesApi: QuotesApi) {

    private val _quoteStateFlow = MutableStateFlow<NetworkResult<QuotesList>>(NetworkResult.Loading())
    val quoteStateFlow: StateFlow<NetworkResult<QuotesList>>
        get() = _quoteStateFlow

    suspend fun getQuotes(page: Int, list: List<Quote>){
        _quoteStateFlow.value = NetworkResult.Loading()
        try {
            val response = quotesApi.getQuotes(page)
            if(response.isSuccessful || response.body() != null){
                Log.d("Response", response.body()!!.toString())
                _quoteStateFlow.value = NetworkResult.Success(response.body()!!)
            } else {
                if(response.errorBody() != null) {
                    Log.e("Error body", response.errorBody()!!.string())
                }
                _quoteStateFlow.value = NetworkResult.Error("Something went wrong!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _quoteStateFlow.value = NetworkResult.Error("Something went wrong!")
        }
    }


}