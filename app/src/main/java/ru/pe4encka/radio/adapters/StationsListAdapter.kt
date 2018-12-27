package ru.pe4encka.radio.adapters

import ru.pe4encka.radio.R
import ru.pe4encka.radio.databinding.CardStationListBinding
import ru.pe4encka.radio.models.RecyclerModel
import ru.pe4encka.radio.models.StationModel

class StationsListAdapter : CategoryAdapter<RecyclerModel, CardStationListBinding>(
    R.layout.card_station_list
) {
    var onLikeClick: (StationModel, Boolean) -> Unit = {_,_ ->}
    override fun onBindViewHolder(holder: CategoryHolder<CardStationListBinding>, position: Int) =
        with(holder.binding) {
            holder.itemView.setOnClickListener { onItemClick(position) }
            checkLike.setOnClickListener { onLikeClick(getItem(position).station, checkLike.isChecked) }
            model = getItem(position)
            executePendingBindings()
        }
}