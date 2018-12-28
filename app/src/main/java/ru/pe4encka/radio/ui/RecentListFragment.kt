package ru.pe4encka.radio.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import ru.pe4encka.radio.R
import ru.pe4encka.radio.adapters.BaseSwipeDragHelper
import ru.pe4encka.radio.adapters.RecentListAdapter
import ru.pe4encka.radio.databinding.FragmentRecentListBinding
import ru.pe4encka.radio.models.PlayerModel

class RecentListFragment : BaseFragment() {
    private lateinit var binding: FragmentRecentListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recent_list, container, false)
        binding.apply {
            viewModel = model
            recycler.adapter = RecentListAdapter().apply {
                onItemClick = { i ->
                    onStationClick(getItem(i).station)
                    PlayerModel.stop()
                    PlayerModel.currentRecyclerItem = getItem(i)
                }
                ItemTouchHelper(BaseSwipeDragHelper(model::onSwipeRecentItem)).apply {
                    attachToRecyclerView(binding.recycler)
                }
            }
            recycler.layoutManager = LinearLayoutManager(activity)
        }
        return binding.root
    }

}
