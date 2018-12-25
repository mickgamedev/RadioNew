package ru.pe4encka.radio.ui


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.pe4encka.radio.R
import ru.pe4encka.radio.adapters.StationsListAdapter
import ru.pe4encka.radio.databinding.FragmentStationsListBinding
import ru.pe4encka.radio.models.PlayerModel
import ru.pe4encka.radio.models.StationModel
import ru.pe4encka.radio.repository.Repository
import ru.pe4encka.radio.service.ACTION_START_FOREGROUND_SERVICE
import ru.pe4encka.radio.service.ACTION_STOP_FOREGROUND_SERVICE
import ru.pe4encka.radio.service.EXTRA_FILENAME_ID
import ru.pe4encka.radio.service.RadioService
import ru.pe4encka.radio.viewmodel.MainViewModel

class StationsListFragment : Fragment() {
    private lateinit var binding: FragmentStationsListBinding
    private lateinit var model: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        model = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stations_list, container, false)
        binding.apply {
            viewModel = model
            recycler.adapter = StationsListAdapter().apply {
                onItemClick = { i ->
                    onStationClick(getItem(i).station)
                    PlayerModel.stop()
                    PlayerModel.currentRecyclerItem = getItem(i)
                }
            }
            recycler.layoutManager = LinearLayoutManager(activity).apply {
                scrollToPositionWithOffset(Repository.currentRecyclerPosition, 0)
            }

            recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val i = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    model.onViewFirstItem(i < 20)
                }
            })

        }

        model.scrollToUp = {
            (binding.recycler.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(0, 0)
        }

        return binding.root
    }

    fun onStationClick(station: StationModel) {
        val act = if (PlayerModel.isPlaying) {
            if (PlayerModel.currentPlay == station) ACTION_STOP_FOREGROUND_SERVICE else ACTION_START_FOREGROUND_SERVICE
        } else ACTION_START_FOREGROUND_SERVICE

        PlayerModel.currentPlay = station

        Intent(activity, RadioService::class.java).apply {
            action = act
            putExtra(EXTRA_FILENAME_ID, station.stream)
            activity?.startService(this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        PlayerModel.currentRecyclerItem = null
        Repository.currentRecyclerPosition =
                (binding.recycler.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        Repository.saveRecyclerPosition(activity!!)
        model.scrollToUp = {}
    }
}
