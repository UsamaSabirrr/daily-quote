package com.example.myapplication.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.preference.PreferenceDataStore
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapplication.data.QuoteRepositoryImpl
import com.example.myapplication.domain.Quote
import com.example.myapplication.domain.QuoteRepository
import com.example.myapplication.view.PreferencesKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val TAG = "ViewModel"

class QuoteViewModel(private val workManager: WorkManager,private val quoteRepository: QuoteRepositoryImpl,private val datastore: DataStore<Preferences>) : ViewModel() {
    private val _state = MutableStateFlow(QuoteState())
    val state: StateFlow<QuoteState> = _state.asStateFlow()
    var quotesCount = 1


    init {
        viewModelScope.launch(Dispatchers.IO) {
          val result = quoteRepository.getAllQuotesLocally()
            Log.d("ViewModel","quotes list is $result")
            if(result.isNotEmpty()){
                _state.value = _state.value.copy(quoteList = result)
            }
        }
    }

    suspend fun visible(){
        _state.value = _state.value.copy(isVisible = true)
        delay(500)
        _state.value = _state.value.copy(isVisible = false)
    }

     fun processIntent(intent: QuoteIntent){
        when(intent){
           // is QuoteIntent.FetchQuote -> getQuote()
            is QuoteIntent.CopyQuoteToClipBoard -> copyQuoteToClipBoard(intent.context)
            is QuoteIntent.ChangeQuoteColor -> updateQuoteColor(intent.color)
            is QuoteIntent.SetCurrentQuoteIndex -> setCurrentQuoteIndex(intent.index)
            is QuoteIntent.CompleteOnboarding -> completeOnboarding()

        }
    }

    private fun updateQuoteColor(color: Color){
        _state.value = _state.value.copy(quoteColor = color)
    }

    private fun copyQuoteToClipBoard(context: Context){
        val service = context.getSystemService(Context.CLIPBOARD_SERVICE)
        val clipboardManager = service as ClipboardManager
        val clipData = ClipData.newPlainText("quote", "${_state.value.quote?.quote}\n\n${_state.value.quote?.author}")
        clipboardManager.setPrimaryClip(clipData)
    }

    private fun setCurrentQuoteIndex(index:Int){
        _state.value = _state.value.copy(quoteIndex = index)
    }

    fun completeOnboarding() {
        val currentList = _state.value.quoteList
        val subList = currentList.take(quotesCount).toList()

        _state.value = _state.value.copy(quoteList = subList)
        saveQuoteList()
    }




    suspend fun getNewQuotes(dataStore: DataStore<Preferences>){
        try {
            val quotesList = quoteRepository.getAllQuotesOnline()
            var quotesCount = 0

            dataStore.data.first().let{it ->
                quotesCount = it[PreferencesKeys.QUOTES_COUNT]?:0
            }

            if (quotesList.isNotEmpty()){
                val trimmedList = quotesList.subList(0,quotesCount)
                quoteRepository.deleteQuotes()
                quoteRepository.saveQuotesList(trimmedList)
                _state.value = _state.value.copy(quoteList = trimmedList)
            }
        }catch (e:Exception){

        }
    }

//    fun setQuoteAsWallpaper(context: Context,bimap:Bitmap){
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val bitmap = createQuoteBitmap(context, "Hy man how are you",)
//                val wallpaperManager = WallpaperManager.getInstance(context)
//                wallpaperManager.setBitmap(bitmap)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }

    private fun fetchQuote(){
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()
        val quoteWorker = OneTimeWorkRequestBuilder<QuoteWorker>().setConstraints(constraints)
        workManager.enqueue(quoteWorker.build())
    }

//    fun getQuote(){
//        viewModelScope.launch(Dispatchers.IO) {
//            _state.value = _state.value.copy(isLoading = true)
//           val result = quoteRepository.getAllQuotesOnline()
//            val users = result.getOrNull()
//            if (users!=null){
//                _state.value = _state.value.copy(quoteList = users, isLoading = false)
//                saveQuoteList()
//            }
//
////            if (users != null) {
////                quoteDao.insertQuote(com.example.myapplication.network.QuoteLocal(uid = users.id, quote = users.title))
////            }
//        }
//    }

    private fun saveQuoteList(){
        viewModelScope.launch(Dispatchers.IO) {
            _state.value.quoteList?.let { quoteRepository.saveQuotesList(
                quotes = it
            ) }
        }
    }

}