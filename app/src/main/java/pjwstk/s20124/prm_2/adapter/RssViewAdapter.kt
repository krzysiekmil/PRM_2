package pjwstk.s20124.prm_2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import pjwstk.s20124.prm_2.R
import pjwstk.s20124.prm_2.databinding.ListRowBinding
import pjwstk.s20124.prm_2.model.RssItem

class RssViewAdapter(private val listener: RowClickListener): RecyclerView.Adapter<RssViewHolder>() {

    var items: List<RssItem> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RssViewHolder {
        val binding = ListRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RssViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RssViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}