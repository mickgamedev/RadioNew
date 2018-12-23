package ru.pe4encka.radio.adapters

import ru.pe4encka.radio.R
import ru.pe4encka.radio.databinding.CardStationListBinding
import ru.pe4encka.radio.models.RecyclerModel

class StationsListAdapter : CategoryAdapter<RecyclerModel, CardStationListBinding>(
    R.layout.card_station_list
) {
    override fun onBindViewHolder(holder: CategoryHolder<CardStationListBinding>, position: Int) =
        with(holder.binding) {
            holder.itemView.setOnClickListener { onItemClick(position) }
            model = getItem(position)
            executePendingBindings()
        }
}