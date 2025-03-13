package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import androidx.work.WorkManager
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.QuoteMapper
import com.example.myapplication.data.QuoteRepositoryImpl
import com.example.myapplication.network.RetrofitClient
import com.example.myapplication.presentation.QuoteIntent
import com.example.myapplication.presentation.QuoteViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.view.OnboardingScreent
import com.example.myapplication.view.PreferencesKeys
import com.example.myapplication.view.QuoteScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null
)



class MainActivity : ComponentActivity() {
    val workManager = WorkManager.getInstance()

    private val USER_PREFERENCES_NAME = "user_preferences"


    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = USER_PREFERENCES_NAME)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "quote-database"
            ).build()
            val api = RetrofitClient.apiService
            val quoteMapper = QuoteMapper()
            val quoteRepository = QuoteRepositoryImpl(db, api, quoteMapper)
            // In MainActivity
            val viewModel: QuoteViewModel by viewModels {
                QuoteViewModelFactory(workManager, quoteRepository, dataStore)
            }

            val state by viewModel.state.collectAsState()
            val scope = CoroutineScope(Dispatchers.Main)
            val context = LocalContext.current

            val items = listOf(
                BottomNavigationItem(
                    title = "Quotes",
                    selectedIcon = Icons.Filled.Home,
                    unselectedIcon = Icons.Outlined.Home,
                    hasNews = false,
                ),
                BottomNavigationItem(
                    title = "History",
                    selectedIcon = Icons.Filled.Email,
                    unselectedIcon = Icons.Outlined.Email,
                    hasNews = false,
                    badgeCount = 45
                ),
                BottomNavigationItem(
                    title = "Favorites",
                    selectedIcon = Icons.Filled.Settings,
                    unselectedIcon = Icons.Outlined.Settings,
                    hasNews = true,
                ),
            )
            var selectedItemIndex by rememberSaveable {
                mutableStateOf(0)
            }
            var onboardingDone by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                dataStore.data.collect{it->
                  onboardingDone =  it[PreferencesKeys.ONBOARDING]?:false
                }

            }

            LaunchedEffect(Unit) {
                Log.d("MainActivity","in it launch effect")
                dataStore.data.first().let { it ->
                    onboardingDone = it[PreferencesKeys.ONBOARDING] ?: false

                }

            }

            Log.d("MainActivity","in it ${onboardingDone}")
            Log.d("MainActivity","quotes list ${state.quoteList.size}")
            MyApplicationTheme {
                when (onboardingDone) {
                    false -> Scaffold {it->

                        OnboardingScreent(onBoardingDone = {

                            scope.launch(Dispatchers.IO) {
                                context.dataStore.edit { it ->
                                    it[PreferencesKeys.ONBOARDING] = true
                                    it[PreferencesKeys.QUOTES_COUNT] = viewModel.quotesCount
                                }
                                onboardingDone = true
                                viewModel.processIntent(QuoteIntent.CompleteOnboarding)

                            }

                        }, viewModel = viewModel)
                    }

                    true -> Scaffold(modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            NavigationBar {
                                items.forEachIndexed { index, item ->
                                    NavigationBarItem(
                                        selected = selectedItemIndex == index,
                                        onClick = {
                                            selectedItemIndex = index
                                            // navController.navigate(item.title)
                                        },
                                        label = {
                                            Text(text = item.title)
                                        },
                                        alwaysShowLabel = false,
                                        icon = {

                                            Icon(
                                                imageVector = if (index == selectedItemIndex) {
                                                    item.selectedIcon
                                                } else item.unselectedIcon,
                                                contentDescription = item.title
                                            )

                                        }
                                    )
                                }


                            }
                        }) { it ->
                        QuoteScreen(
                            viewModel = viewModel,
                            dataStore,
                            quoteColor = state.quoteColor,
                            changeQuoteColor = { color ->
                                scope.launch {
                                    viewModel.processIntent(QuoteIntent.ChangeQuoteColor(color))
                                }

                            },
                            copyQuoteToClipBoard = {
                                scope.launch {
                                    viewModel.processIntent(
                                        QuoteIntent.CopyQuoteToClipBoard(
                                            context = context,
                                        )
                                    )
                                }

                            },
                        )

//                            Button(onClick = {
//                                val config = Configuration.Builder()
//                                    .setMinimumLoggingLevel(Log.DEBUG)
//                                    .setExecutor(SynchronousExecutor())
//                                    .build()
//
//                                WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
//
//                                val testWorker = PeriodicWorkRequestBuilder<QuoteWorker>(
//                                    1, TimeUnit.MINUTES  // Much shorter interval for testing
//                                ).build()
//                                workManager.enqueue(testWorker)
//                            }) {
//                                Text("Click me")
//                            }
                    }


                }
            }
        }
    }
}




//@Composable
//fun NavigationHost(navController: NavController) {
//    NavHost(navController, startDestination = BottomNavItem.Home.route) {
//        composable(BottomNavItem.Home.route) { /* Home Screen UI */ }
//        composable(BottomNavItem.Search.route) { /* Search Screen UI */ }
//        composable(BottomNavItem.Profile.route) { /* Profile Screen UI */ }
//    }
//}









//will need to add these changes too

//@Composable
//fun QuoteUI(state: QuoteState){
//    Column(
//        modifier = Modifier.fillMaxHeight().fillMaxWidth(),
//        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
//        if(state.isLoading){
//            CircularProgressIndicator()
//        }else {
//            Column {
//                Button(
//                    onClick = {
//                        scope.launch(Dispatchers.IO) {
//                            viewModel.processIntent(QuoteIntent.FetchQuote)
//                        }
//                    }) {
//                    Text("Fetch Latest")
//                }
//                Text(state.quote?.title ?: "")
//            }
//            Column {
//                for (i in 0 until (state.quoteList?.size ?: 0))
//                    state.quoteList?.get(i)?.quote?.let { Text(it) }
//            }
//        }
//    }
//}


class QuoteViewModelFactory(
    private val workManager: WorkManager,
    private val repository: QuoteRepositoryImpl,
    private val dataStore: DataStore<Preferences>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuoteViewModel(workManager, repository, dataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}