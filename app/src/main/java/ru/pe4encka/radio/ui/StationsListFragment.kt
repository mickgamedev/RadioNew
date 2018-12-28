package ru.pe4encka.radio.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.pe4encka.radio.R
import ru.pe4encka.radio.adapters.StationsListAdapter
import ru.pe4encka.radio.databinding.FragmentStationsListBinding
import ru.pe4encka.radio.models.PlayerModel
import ru.pe4encka.radio.repository.Repository

class StationsListFragment : BaseFragment() {
    private lateinit var binding: FragmentStationsListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stations_list, container, false)
        binding.apply {
            viewModel = model
            recycler.adapter = StationsListAdapter().apply {
                onItemClick = { i ->
                    onStationClick(getItem(i).station)
                    PlayerModel.stop()
                    PlayerModel.currentRecyclerItem = getItem(i)
                }
                onLikeClick = {s,p -> model.onLikeClick(s,p)}
                model.catalogAdapter = this
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

    override fun onDestroyView() {
        super.onDestroyView()
        Repository.currentRecyclerPosition =
                (binding.recycler.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        Repository.saveRecyclerPosition(activity!!)
        model.scrollToUp = {}
        model.catalogAdapter = null
    }
}
