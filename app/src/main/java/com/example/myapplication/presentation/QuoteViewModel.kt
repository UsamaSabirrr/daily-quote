package com.example.myapplication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapplication.domain.Quote
import com.example.myapplication.network.QuoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class QuoteViewModel(private val workManager: WorkManager,private val quoteRepository: QuoteRepository) : ViewModel() {
    private val _state = MutableStateFlow(QuoteState())
    val state: StateFlow<QuoteState> = _state.asStateFlow()



    val myQuotes = listOf("Hy man","How are you","doing good")

    suspend fun processIntent(intent: QuoteIntent){
        when(intent){
            is QuoteIntent.FetchQuote -> getQuote()
        }
    }

    private fun fetchQuote(){
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()
        val quoteWorker = OneTimeWorkRequestBuilder<QuoteWorker>().setConstraints(constraints)
        workManager.enqueue(quoteWorker.build())
    }

    fun getQuote(){
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(isLoading = true)
           val result = quoteRepository.getUser()
            val users = result.getOrNull()

            _state.value = _state.value.copy(users?.title?.let {
                Quote(
                    title = it,
                    id ="2"
                )
            }, isLoading = false)
        }
    }

}