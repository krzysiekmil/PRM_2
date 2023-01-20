package pjwstk.s20124.prm_2.adapter

import pjwstk.s20124.prm_2.model.RssItem


interface RowClickListener{
    fun onItemClickListener(item: RssItem)
    fun onLongClickListener(item: RssItem)
}