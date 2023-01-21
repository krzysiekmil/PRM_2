package pjwstk.s20124.prm_2.information

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
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
    private var favouriteMode = false

    private lateinit var recyclerViewAdapter: RssViewAdapter
    private lateinit var recyclerView: RecyclerView

    private val rssViewModel: RssViewModel by lazy { ViewModelProvider(this, RssViewModelFactory(application as RssApplication))[RssViewModel::class.java] }
    val database = Firebase.database



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
            if(favouriteMode) {
                recyclerViewAdapter.items = it.filter { rssItem -> rssItem.favourite }
            } else {
                recyclerViewAdapter.items = it
            }
            recyclerViewAdapter.notifyDataSetChanged()
        }


        val bottomNavigation = binding.bottomNavigation

        bottomNavigation.setOnItemSelectedListener { item ->
            when(item.title) {
               "List" -> {
                    favouriteMode = false
                    rssViewModel.fetch()
                    true
                }
                "Favourites" -> {
                    favouriteMode = true
                    rssViewModel.fetch()
                    true
                }
                else -> {
                    false
                }
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
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra("id", item.id)
        startActivity(intent)
    }

    override fun onLongClickListener(item: RssItem) {
        val favourite = item.favourite
        val uuid = item.id
        val action = if (!favourite) "add to" else "remove from"
        MaterialAlertDialogBuilder(this)
            .setMessage("Do you want $action favourites")
            .setTitle("Favourites changes")
            .setCancelable(false)
            .setPositiveButton("Yes") { _: DialogInterface?, _: Int ->  if(!favourite) rssViewModel.addToFavourites(uuid) else rssViewModel.removeFromFavourites(uuid)}
            .setNegativeButton("No") { dialog: DialogInterface, _: Int -> dialog.cancel() }
            .create().show()
    }
}