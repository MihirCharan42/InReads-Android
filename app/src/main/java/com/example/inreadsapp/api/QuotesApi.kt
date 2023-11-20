package com.example.inreadsapp.api

import APIs
import com.example.inreadsapp.models.QuotesList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface QuotesApi {

    @GET(APIs.getQuotes)
    suspend fun getQuotes(@Query("page") page: Int): Response<QuotesList>
}