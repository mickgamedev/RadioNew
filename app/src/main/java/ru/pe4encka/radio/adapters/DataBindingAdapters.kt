package ru.pe4encka.radio.adapters

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.pe4encka.radio.R
import ru.pe4encka.radio.models.PlayerModel
import ru.pe4encka.radio.models.RecyclerModel
import ru.pe4encka.radio.models.StationModel
import ru.pe4encka.radio.repository.Repository

@BindingAdapter("visibility")
fun View.SetVisibitily(b: Boolean) {
    visibility = if (b) View.VISIBLE else View.GONE
}

@BindingAdapter("visibility_fade")
fun View.SetVisibitilyFade(b: Boolean) {
    alpha = if (b) 0.3f else 1.0f
}

@BindingAdapter("background_select")
fun View.SelectBackground(b: Boolean){
    background = if(b) {
        elevation = 5.0f
        resources.getDrawable(R.color.colorBackgroundDark)
    } else {
        elevation = 0.0f
        resources.getDrawable(R.color.colorBackgroundLite)
    }
}

@BindingAdapter("text")
fun TextView.setTextVisibility(st: String) {
    if (st.trim().isEmpty()) visibility = View.GONE
    else {
        visibility = View.VISIBLE
        text = st
    }
}

@BindingAdapter("stations_list")
fun RecyclerView.setStationList(lib: List<StationModel>?) {
    lib ?: return
    adapter ?: return
    (adapter as StationsListAdapter).setItems(lib.map { RecyclerModel(it) }.apply { PlayerModel.findRecyclerItem(this) })
    (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(Repository.currentRecyclerPosition, 0)
}

@BindingAdapter("recent_list")
fun RecyclerView.setRecentList(lib: List<StationModel>?) {
    lib ?: return
    adapter ?: return
    (adapter as RecentListAdapter).setItems(lib.map { RecyclerModel(it) }.apply { PlayerModel.findRecyclerItem(this) })
}


@BindingAdapter("src")
fun ImageView.setBitmap(bitmap: Bitmap?) {
    bitmap ?: let {
        //visibility = View.GONE
        return
    }
    setImageBitmap(bitmap)
}

@BindingAdapter("src")
fun ImageView.setSourceImage(src: String?) {
    src ?: let {
        //visibility = View.GONE
        return
    }
    if (!src.isEmpty()) Picasso.get().load(src).into(this)
}

@BindingAdapter("onSearch")
fun SearchView.setOnSearch(listener: (String) -> Unit) {
    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            listener("")
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            return true
        }

    })

    setOnCloseListener {
        listener("")
        false
    }
}