package ru.pe4encka.radio.adapters

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.pe4encka.radio.R
import ru.pe4encka.radio.databinding.CardStationListBinding
import ru.pe4encka.radio.models.RecyclerModel

class RecentListAdapter : CategoryAdapter<RecyclerModel, CardStationListBinding>(
    R.layout.card_station_list
) {
    override fun onBindViewHolder(holder: CategoryHolder<CardStationListBinding>, position: Int) =
        with(holder.binding) {
            holder.itemView.setOnClickListener { onItemClick(position) }
            model = getItem(position)
            executePendingBindings()
        }

    fun onSwipe(i: Int) {
        removeItem(i)
        notifyItemRemoved(i);
    }

}

class BaseSwipeDragHelper(val onSwipe: (Int) -> Unit) : ItemTouchHelper.Callback() {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        val dragFlags = 0
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        onSwipe(position)
    }
}