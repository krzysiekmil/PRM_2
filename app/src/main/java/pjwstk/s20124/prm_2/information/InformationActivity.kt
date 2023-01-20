package pjwstk.s20124.prm_2.information

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.common.io.Resources.getResource
import com.google.firebase.auth.FirebaseAuth
import io.grpc.InternalChannelz.id
import pjwstk.s20124.prm_2.RssApplication
import pjwstk.s20124.prm_2.adapter.RowClickListener
import pjwstk.s20124.prm_2.adapter.RssViewAdapter
import pjwstk.s20124.prm_2.authentication.AuthenticationActivity
import pjwstk.s20124.prm_2.databinding.ActivityInformationBinding
import pjwstk.s20124.prm_2.model.RssItem
import pjwstk.s20124.prm_2.model.RssSchema
import pjwstk.s20124.prm_2.utils.XmlParser
import pjwstk.s20124.prm_2.viewModel.RssViewModel
import pjwstk.s20124.prm_2.viewModel.RssViewModelFactory
import java.net.URL
import java.util.concurrent.Executors


class InformationActivity : AppCompatActivity(), RowClickListener {

    private lateinit var binding: ActivityInformationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var GEO_PERMISSION_REQUEST_CODE = 1

    private lateinit var recyclerViewAdapter: RssViewAdapter
    private lateinit var recyclerView: RecyclerView

    private val rssViewModel: RssViewModel by lazy { ViewModelProvider(this, RssViewModelFactory(application as RssApplication))[RssViewModel::class.java] }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        binding.logOutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, AuthenticationActivity::class.java))
        }

        recyclerView = binding.recyclerView

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@InformationActivity)
            recyclerViewAdapter = RssViewAdapter(this@InformationActivity)
            adapter = recyclerViewAdapter
        }

        rssViewModel.rssItemList.observe(this) {
            recyclerViewAdapter.items = it
            recyclerViewAdapter.notifyDataSetChanged()
        }

        val bottomNavigation = binding.bottomNavigation

        bottomNavigation.setOnNavigationItemReselectedListener { item ->
            when(item.itemId) {
                1 -> {
                    // Respond to navigation item 1 reselection
                }
                2 -> {
                    // Respond to navigation item 2 reselection
                }
            }
        }




//        binding.geo.setOnClickListener {
//            downloadRSS()
//            getLastLocation()
//        }
    }

    private fun downloadRSS(){
        val myExecutor = Executors.newSingleThreadExecutor()
        val myHandler = Handler(Looper.getMainLooper())
        var rss: RssSchema? = null
        myExecutor.execute {
            rss = XmlParser.getInstance()?.parser?.readValue(URL("https://wiadomosci.gazeta.pl/pub/rss/wiadomosci_kraj.htm"), RssSchema::class.java)!!
            myHandler.post {
                recyclerViewAdapter.items = rss?.channel?.items as ArrayList<RssItem>
                recyclerViewAdapter.notifyDataSetChanged()
            }
        }

    }


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {

            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation
                    .addOnCompleteListener {
                        val location = it.result
                        if (location == null) {
                            requestNewLocationData()
                        } else {
                            val geocoder = Geocoder(this)
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)

                        }

                    }
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show()
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {

        val mLocationRequest = LocationRequest.Builder(LocationRequest.PRIORITY_HIGH_ACCURACY, 5)
            .setMaxUpdates(5)
            .build()

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            {
            it.longitude
            },
            Looper.myLooper()
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
         locationResult.lastLocation

        }
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), GEO_PERMISSION_REQUEST_CODE)
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onItemClickListener(item: RssItem) {
        TODO("Not yet implemented")
    }

    override fun onLongClickListener(item: RssItem) {
        TODO("Not yet implemented")
    }
}