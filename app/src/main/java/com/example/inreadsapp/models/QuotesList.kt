package com.example.inreadsapp.models

data class QuotesList(
    val count: Int,
    val lastItemIndex: Int,
    val page: Int,
    var results: MutableList<Quote>,
    val totalCount: Int,
    val totalPages: Int
)