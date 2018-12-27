package ru.pe4encka.radio.viewmodel

import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.pe4encka.radio.adapters.StationsListAdapter
import ru.pe4encka.radio.models.StationModel
import ru.pe4encka.radio.repository.Repository
import ru.pe4encka.radio.repository.findRecyclerModelByID
import ru.pe4encka.radio.repository.findStationModelByID

class MainViewModel : ViewModel() {
    private val viewModelJob = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)
    var scrollToUp: () -> Unit = {}

    val stations = ObservableField(Repository.stations)
    val recentStations = ObservableField<List<StationModel>>()

    val parseStation = ObservableBoolean(false)

    val showSearch = ObservableBoolean(true)
    val showUpScroll = ObservableBoolean(false)
    private val disp: Disposable
    private var tempShowUpScroll: Boolean = false

    var catalogAdapter: StationsListAdapter? = null

    init {
        if (stations.get() == null) {
            parseStation.set(true)
            ioScope.launch {
                Repository.parseStationList()
                stations.set(Repository.stations)
                parseStation.set(false)
            }
        }
        disp = Repository.getRecentObserver().subscribe {
            recentStations.set(it)
        }
    }

    fun toUP() = scrollToUp()

    fun onViewFirstItem(b: Boolean) {
        showUpScroll.set(!b)
    }

    fun onAddRecentStation(station: StationModel) {
        ioScope.launch {
            station.recent = true
            catalogAdapter?.getItems()?.findRecyclerModelByID(station.id)?.showLike?.set(station.recent)
            Repository.updateRecentStation(station)
        }
    }

    fun onSearch(query: String) {
        Log.v("MainView", "OnSearch $query")
        if (query.isEmpty()) stations.set(Repository.stations)
        else stations.set(Repository.stations?.filter {
            it.name.toLowerCase().contains(query.toLowerCase()) ||
                    it.locate.toLowerCase().contains(query.toLowerCase()) ||
                    it.tags.toLowerCase().contains(query.toLowerCase()) ||
                    it.format.toLowerCase().contains(query.toLowerCase())
        })
    }

    fun onSelectTab(i: Int) {
        when (i) {
            0 -> {
                showSearch.set(true)
                showUpScroll.set(tempShowUpScroll)
            }
            1 -> {
                showUpScroll.set(false)
                showSearch.set(false)
                tempShowUpScroll = showUpScroll.get()
            }
        }
    }

    fun onSwipeRecentItem(i: Int) {
        val list = recentStations.get()
        list ?: return
        onRemoveRecentStation(list[i])
    }

    fun onRemoveRecentStation(station: StationModel){
        ioScope.launch {
            station.recent = false
            catalogAdapter?.getItems()?.findRecyclerModelByID(station.id)?.showLike?.set(station.recent)
            stations.get()?.findStationModelByID(station.id)?.recent = station.recent
            Repository.updateRecentStation(station)
        }
    }

    fun onLikeClick(station: StationModel, state: Boolean){
        if(state){
            onAddRecentStation(station)
        } else {
            onRemoveRecentStation(station)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        disp.dispose()
    }
}