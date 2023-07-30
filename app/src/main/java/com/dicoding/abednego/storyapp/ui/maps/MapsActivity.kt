package com.dicoding.abednego.storyapp.ui.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.abednego.storyapp.R
import com.dicoding.abednego.storyapp.data.datastore.UserPreferences
import com.dicoding.abednego.storyapp.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.dicoding.abednego.storyapp.data.api.response.ListStoryItem
import com.dicoding.abednego.storyapp.ui.detail.DetailStoryActivity
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var userPreferences: UserPreferences
    private lateinit var mapsViewModel: MapsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreferences = UserPreferences(applicationContext)
        mapsViewModel = ViewModelProvider(this)[MapsViewModel::class.java]

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()

        userPreferences.isLogin.onEach { token ->
            if (token.isNotEmpty()) {
                mapsViewModel.getAllStories(token)
            }
        }.launchIn(lifecycleScope)

        val boundsBuilder = LatLngBounds.builder()

        mapsViewModel.storyList.observe(this) { stories ->
            for (story in stories) {
                val storyLatLng = LatLng(story.lat ?: 0.0, story.lon ?: 0.0)
                val markerOptions = MarkerOptions()
                    .position(storyLatLng)
                    .title(story.name)
                    .snippet(story.description)
                mMap.addMarker(markerOptions)?.tag = story
                boundsBuilder.include(storyLatLng)
            }

            val bounds = boundsBuilder.build()
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
            mMap.moveCamera(cameraUpdate)

            mMap.setOnMarkerClickListener { marker ->

                val zoom = mMap.cameraPosition.zoom
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.position,12f))
                marker.showInfoWindow()

                val story = marker.tag as? ListStoryItem
                marker.showInfoWindow()

                if (zoom>10 && story != null) {
                    val intent = Intent(this@MapsActivity, DetailStoryActivity::class.java).apply {
                        putExtra(EXTRA_NAME, story.name)
                        putExtra(EXTRA_PHOTO, story.photoUrl)
                        putExtra(EXTRA_DESCRIPTION, story.description)
                    }
                    startActivity(intent)
                }
                true
            }
        }

        mapsViewModel.errorMessage.observe(this){message ->
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options, menu)
        return true
    }

    @Suppress("DEPRECATION")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
    companion object {
        const val EXTRA_NAME = "EXTRA_NAME"
        const val EXTRA_PHOTO = "EXTRA_PHOTO"
        const val EXTRA_DESCRIPTION = "EXTRA_DESCRIPTION"
    }
}