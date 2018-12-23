package ru.pe4encka.radio.adapters

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.pe4encka.radio.models.PlayerModel
import ru.pe4encka.radio.models.RecyclerModel
import ru.pe4encka.radio.models.StationModel

@BindingAdapter("visibility")
fun View.SetVisibitily(b: Boolean) {
    visibility = if (b) View.VISIBLE else View.GONE
}

@BindingAdapter("visibility_fade")
fun View.SetVisibitilyFade(b: Boolean) {
    alpha = if (b) 0.3f else 1.0f
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
fun RecyclerView.setLibrary(lib: List<StationModel>?) {
    lib ?: return
    adapter ?: return
    (adapter as StationsListAdapter).setItems(lib.map { RecyclerModel(it) }.apply { PlayerModel.findRecyclerItem(this) })
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
