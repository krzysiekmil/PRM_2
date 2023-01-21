package pjwstk.s20124.prm_2.adapter

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.view.View
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import org.apache.commons.lang.StringEscapeUtils
import org.apache.commons.lang3.StringUtils
import pjwstk.s20124.prm_2.databinding.ListRowBinding
import pjwstk.s20124.prm_2.model.RssItem
import java.net.URL
import java.util.concurrent.Executors

class RssViewHolder(binding: ListRowBinding): RecyclerView.ViewHolder(binding.root) {

    private var image = binding.rowImage
    private var title = binding.rowTitle
    private var description = binding.rowDescription


    fun bind(item: RssItem){
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute {
            val inputStream = URL(item.enclosure.url).openStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)

            handler.post {
                image.setImageBitmap(bitmap)
            }
        }

        title.text = item.title
        description.text = Html.fromHtml(item.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
            .toString()
            .substring(1)

        if(item.wasRead){
            title.setTextColor(Color.LTGRAY)
            description.setTextColor(Color.LTGRAY)
        }

    }
}