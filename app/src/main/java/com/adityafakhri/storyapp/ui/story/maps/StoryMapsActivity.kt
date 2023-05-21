package com.adityafakhri.storyapp.ui.story.maps

import android.Manifest.permission
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.adityafakhri.storyapp.R
import com.adityafakhri.storyapp.data.source.local.AuthPreferences
import com.adityafakhri.storyapp.data.source.local.dataStore
import com.adityafakhri.storyapp.data.viewmodel.AuthViewModel
import com.adityafakhri.storyapp.data.viewmodel.StoryMapsViewModel
import com.adityafakhri.storyapp.data.viewmodel.ViewModelAuthFactory
import com.adityafakhri.storyapp.data.viewmodel.ViewModelGeneralFactory
import com.adityafakhri.storyapp.databinding.ActivityStoryMapsBinding
import com.adityafakhri.storyapp.utils.Const
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class StoryMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private var _binding: ActivityStoryMapsBinding? = null
    private val binding get() = _binding!!

    private var viewModel: StoryMapsViewModel? = null
    private var tokenKey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityStoryMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        val titleName = getString(R.string.title_activity_story_maps)
        actionBar?.title = titleName

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val pref = AuthPreferences.getInstance(dataStore)
        val authViewModel =
            ViewModelProvider(this, ViewModelAuthFactory(pref))[AuthViewModel::class.java]

        viewModel =
            ViewModelProvider(this, ViewModelGeneralFactory(this))[StoryMapsViewModel::class.java]

        authViewModel.getUserPreferences(Const.UserPreferences.Token.name).observe(this) { token ->
            if (token != "Not Set") {
                tokenKey = StringBuilder("Bearer ").append(token).toString()
                viewModel?.loadStoryLocationData(tokenKey)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isTiltGesturesEnabled = true

        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(Const.defaultLocation, 4f)
        )

        viewModel?.storyList?.observe(this@StoryMapsActivity) { storyList ->
            for (story in storyList) {
                val latLng = LatLng(story.lat?.toDouble() ?: 0.0, story.lon?.toDouble() ?: 0.0)
                mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(story.name)
                )
            }
        }

        getMyLocation()
        setMapStyle()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            } else {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        finish()
        return true
    }

    companion object {
        private const val TAG = "MapsActivity"
    }

}