package com.example.myapplication.data

import com.example.myapplication.domain.Quote
import com.example.myapplication.domain.QuoteRepository
import com.example.myapplication.network.ApiService
import com.example.myapplication.presentation.quotesList

class QuoteRepositoryImpl(val database: AppDatabase,val api:ApiService,val quoteMapper: QuoteMapper) :QuoteRepository{


    override suspend fun getAllQuotesLocally(): List<Quote> {
        try{
            var quotesEntityList = database.quoteDao().getAllQuotes()
            val quotesList = quotesEntityList.map {
                quoteMapper.mapEntityToDomain(it)
            }
            return quotesList
        }catch (e:Exception){
            return emptyList()
        }

    }


    override suspend fun deleteQuotes() {
        try {
            database.quoteDao().deleteAllQuotes()
        }catch (e:Exception){

        }
    }

    override suspend fun saveQuotesList(quotes:List<Quote>) {
        try {
            val quotedEntityList = quotes.map {
                quoteMapper.mapDomainToEntity(it)
            }
            database.quoteDao().insertQuotesList(quotedEntityList)
        }catch (e:Exception){

        }
    }

    override suspend fun getAllQuotesOnline():List<Quote>{
        try {
            val quotes = api.getQuote()
            val quotesList = quotes.map { it->quoteMapper.mapDtoToDomain(it) }
            return quotesList
        }catch (e:Exception){
            return emptyList()
        }
    }

}