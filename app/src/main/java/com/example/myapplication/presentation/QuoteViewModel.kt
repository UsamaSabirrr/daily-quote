package com.example.myapplication.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapplication.domain.Quote
import com.example.myapplication.network.AppDatabase
import com.example.myapplication.network.QuoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuoteViewModel(private val workManager: WorkManager,private val quoteRepository: QuoteRepository) : ViewModel() {
    private val _state = MutableStateFlow(QuoteState())
    val state: StateFlow<QuoteState> = _state.asStateFlow()


    init {
        viewModelScope.launch(Dispatchers.IO) {
          val result = quoteRepository.getAllQuotes()
            if(result.isNotEmpty()){
                _state.value = _state.value.copy(quoteList = result)
            }
        }
    }


    val myQuotes = listOf("Hy man","How are you","doing good")

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
        }
    }

    private fun updateQuoteColor(color: androidx.compose.ui.graphics.Color){
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

    fun getQuote(){
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(isLoading = true)
           val result = quoteRepository.getUser()
            val users = result.getOrNull()
            if (users!=null){
                _state.value = _state.value.copy(quoteList = users, isLoading = false)
                saveQuoteLocally()
            }

//            if (users != null) {
//                quoteDao.insertQuote(com.example.myapplication.network.QuoteLocal(uid = users.id, quote = users.title))
//            }
        }
    }

    private fun saveQuoteLocally(){
        viewModelScope.launch(Dispatchers.IO) {
            _state.value.quoteList?.let { quoteRepository.saveQuotesLocally(quoteList = it) }
        }
    }

}