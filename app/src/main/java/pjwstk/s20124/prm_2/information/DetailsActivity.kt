package pjwstk.s20124.prm_2.information

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import pjwstk.s20124.prm_2.R
import pjwstk.s20124.prm_2.RssApplication
import pjwstk.s20124.prm_2.databinding.ActivityDetailsBinding
import pjwstk.s20124.prm_2.model.RssItem
import pjwstk.s20124.prm_2.viewModel.RssViewModel
import pjwstk.s20124.prm_2.viewModel.RssViewModelFactory
import java.net.URL
import java.util.*
import java.util.concurrent.Executors


class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private val rssViewModel: RssViewModel by lazy { ViewModelProvider(this, RssViewModelFactory(application as RssApplication))[RssViewModel::class.java] }
    private lateinit var item: RssItem

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        item = rssViewModel.rssItemList.value?.find { it.id == intent.getSerializableExtra("id", UUID::class.java)  }!!

        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute {
            val inputStream = URL(item.enclosure.url).openStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)

            handler.post {
                binding.image.setImageBitmap(bitmap)
            }
        }

        binding.title.text = item.title
        binding.description.text = Html.fromHtml(item.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
            .toString()
            .substring(1)

        binding.link.text = item.link

        binding.link.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.data = Uri.parse(item.link)
            startActivity(intent)
        }

        if(item.favourite) {
            binding.likeButton.setImageResource(R.drawable.ic_baseline_favorite_24)
        }
        else{
            binding.likeButton.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }

        binding.likeButton.setOnClickListener {
            if(item.favourite){
                rssViewModel.removeFromFavourites(item.id)
                binding.likeButton.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                Toast.makeText(this, "Article has been removed from favourites", Toast.LENGTH_LONG).show()
            }
            else{
                rssViewModel.addToFavourites(item.id)
                binding.likeButton.setImageResource(R.drawable.ic_baseline_favorite_24)
                Toast.makeText(this, "Article has been added to favourites", Toast.LENGTH_LONG).show()
            }
        }

        binding.pubDate.text = item.date
        binding.author.text = item.creator

        rssViewModel.setAsRead(item.id)

        binding.shareButton.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, item.link)
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, getString(R.string.share)))
        }
    }
}