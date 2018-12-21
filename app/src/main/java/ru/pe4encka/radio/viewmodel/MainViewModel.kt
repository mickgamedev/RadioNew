package ru.pe4encka.radio.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.pe4encka.radio.repository.Repository

class MainViewModel: ViewModel(){
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val stations = ObservableField(Repository.stations)
    val parseStation = ObservableBoolean(false)

    init {
        if (stations.get() == null) {
            parseStation.set(true)
            uiScope.launch {
                Repository.parseStationList()
                stations.set(Repository.stations)
                parseStation.set(false)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}