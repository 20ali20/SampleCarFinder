package com.alimojarrad.fair.Modules.Main

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.alimojarrad.fair.Models.Result
import com.alimojarrad.fair.Models.ServerResponse
import com.alimojarrad.fair.R
import com.alimojarrad.fair.Services.API.Interfaces.CarResultQueryParam
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.sidecarhealth.Modules.Common.Util
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import java.io.Serializable


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private var locationManager: LocationManager? = null
    private var currentLocation: Location? = null
    private var adapter: ProviderAdapter? = null
    private lateinit var viewModel: ResultViewModel
    private lateinit var mapView: MapView
    private lateinit var mapMarkers: HashMap<MarkerType, Marker>
    private var map: GoogleMap? = null
    private var isAsc = true
    private var currentTabPos = 0

    enum class MarkerType {
        USERADDRESS,
        CARLOCATION,
        CURRENTPOSITION
    }

    companion object {
        const val dataKey = "MainActivityDataKey"
        fun startActivity(context: Context, map: HashMap<String, String>) {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(dataKey, map as Serializable)
            context.startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mapView = this.findViewById(R.id.mainactivity_map)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        mapMarkers = HashMap()
        setupLocationListener()
        setupViews()
        setupInteractions()

    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap
        map?.setMinZoomPreference(12f)

    }

    private fun retrieveMapParams(): HashMap<String, String>? {
        return intent.extras.getSerializable(dataKey) as? HashMap<String, String>
    }

    @SuppressLint("MissingPermission")
    private fun setupLocationListener() {
        val LOCATION_UPDATE_MIN_DISTANCE = 10f
        val LOCATION_UPDATE_MIN_TIME: Long = 5000

        val locationListenerGPS = object : LocationListener {
            override fun onLocationChanged(location: android.location.Location) {
                currentLocation = location
                showLocationOnMap(location, MarkerType.CURRENTPOSITION, "Current Location", null)
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager?.let { locationManager ->
            RxPermissions(this).request(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                if (it) {
                                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                            2000,
                                            10f, locationListenerGPS)
                                }
                            },
                            {
                                Timber.e(it)
                            }
                    )
        }
        locationManager?.let { locationManager ->
            var isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            var isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!(isGPSEnabled || isNetworkEnabled))
            else {
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, locationListenerGPS)
                    currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                }

                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, locationListenerGPS)
                    currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                }
            }
            currentLocation?.let {
                showLocationOnMap(it, MarkerType.CURRENTPOSITION, "Current Location", null)
            }
        }
    }

    private fun setupViews() {
        viewModel = ViewModelProvider.NewInstanceFactory().create(ResultViewModel::class.java)
        retrieveMapParams()?.let {
            try {
                var location = Location("")
                location.latitude = it[CarResultQueryParam.Latitude.q]!!.toDouble()
                location.longitude = it[CarResultQueryParam.Longitutde.q]!!.toDouble()
                showLocationOnMap(location, MarkerType.USERADDRESS, "Provider Address Location", R.drawable.v_address_marker)
            } catch (e: NullPointerException) {

            }

        }
        setupTabs()
        setupViewModelObservers()
        setupRecyclerview()
    }

    private fun setupInteractions() {
        mainactivity_ascdec.setOnClickListener {
            isAsc = !isAsc
            if (isAsc) {
                mainactivity_ascdec.setImageResource(R.drawable.v_asc)
            } else {
                mainactivity_ascdec.setImageResource(R.drawable.v_dec)
            }
            when (currentTabPos) {
                0 -> { // Distance
                    adapter?.let {
                        it.sortBy(ProviderAdapter.SortType.DISTANCE, isAsc)
                    }
                }
                1 -> { // Provider
                    adapter?.let {
                        it.sortBy(ProviderAdapter.SortType.PROVIDER, isAsc)
                    }
                }
            }
        }

        mainactivity_refresh.setOnRefreshListener {
            adapter?.let {
                it.removeAll()
            }
            retrieveMapParams()?.let {
                getResults(it)
            }


        }
    }

    private fun setupTabs() {
        mainactivity_sortby_tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTabPos = tab?.position ?: 0
                when (tab?.position) {
                    0 -> { // Distance
                        adapter?.let {
                            it.sortBy(ProviderAdapter.SortType.DISTANCE, isAsc)
                        }
                    }
                    1 -> { // Provider
                        adapter?.let {
                            it.sortBy(ProviderAdapter.SortType.PROVIDER, isAsc)
                        }
                    }
                }
            }
        })

    }


    private fun setupViewModelObservers() {
        val serverResponseObserver = Observer<ServerResponse> {
            it?.let {
                if (it.isSuccessful == false) {
                    Toast.makeText(this, "${it.reason}", Toast.LENGTH_SHORT).show()
                    mainactivity_refresh.isRefreshing = false
                }
            }
        }
        val resultsObserver = Observer<ArrayList<Result>> {
            it?.let { newResults ->
                adapter?.let {
                    it.updateResults(newResults)
                    mainactivity_refresh.isRefreshing = false
                }
            }
        }

        viewModel?.serverResponse?.observe(this, serverResponseObserver)
        viewModel?.result?.observe(this, resultsObserver)
    }

    private fun setupRecyclerview() {
        adapter = ProviderAdapter(this)
        try {
            val latitude = retrieveMapParams()!![CarResultQueryParam.Latitude.q]!!.toDouble()
            val longitude = retrieveMapParams()!![CarResultQueryParam.Longitutde.q]!!.toDouble()
            var location = Location("")
            location.latitude = latitude
            location.longitude = longitude
            adapter!!.currentLocation = location
        } catch (e: NullPointerException) {

        }

        mainactivity_recyclerview.adapter = adapter
        mainactivity_recyclerview.layoutManager = LinearLayoutManager(this)
        retrieveMapParams()?.let {
            getResults(it)
        }


    }

    private fun getResults(filterMap: HashMap<String, String>) {
        mainactivity_refresh.isRefreshing = true
        viewModel.getResults(filterMap)
    }

    private fun showLocationOnMap(location: Location, type: MarkerType, title: String, drawable: Int?) {
        var latlong = LatLng(location.latitude, location.longitude)

        when (type) {
            MarkerType.USERADDRESS -> {
                mapMarkers[MarkerType.USERADDRESS]?.let {
                    it.remove()
                }
                mapMarkers.remove(MarkerType.USERADDRESS)
            }

            MarkerType.CARLOCATION -> {
                mapMarkers[MarkerType.CARLOCATION]?.let {
                    it.remove()
                }
                mapMarkers.remove(MarkerType.CARLOCATION)
            }

            MarkerType.CURRENTPOSITION -> {
                mapMarkers[MarkerType.CURRENTPOSITION]?.let {
                    it.remove()
                }
                mapMarkers.remove(MarkerType.CURRENTPOSITION)
            }
        }

        var marker = map?.addMarker(
                MarkerOptions()
                        .title(title)
                        .position(latlong)
                        .icon(Util.getMarkerBitmap(this, drawable))
        )
        marker?.let {
            when (type) {
                MarkerType.USERADDRESS -> {
                    mapMarkers[MarkerType.USERADDRESS] = it
                }
                MarkerType.CURRENTPOSITION -> {
                    mapMarkers[MarkerType.CURRENTPOSITION] = it
                }
                MarkerType.CARLOCATION -> {
                    mapMarkers[MarkerType.CARLOCATION] = it
                }
            }
        }

        map?.moveCamera(CameraUpdateFactory.newLatLng(latlong))
    }


    @Subscribe
    fun CarNavigationClickListener(clickEvent: CarClickEvent) {
        when (clickEvent.clickType) {
            ClickType.NAVIGATE -> {
                var gmmIntentUri = Uri.parse("google.navigationKey:q=${clickEvent.latitude},${clickEvent.longitude}")
                var mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.`package` = "com.google.android.apps.maps"
                startActivity(mapIntent)
            }
            ClickType.LOCATE -> {
                var location = Location("")
                location.latitude = clickEvent.latitude
                location.longitude = clickEvent.longitude
                showLocationOnMap(location, MarkerType.CARLOCATION, clickEvent.title, R.drawable.v_car_marker)

            }
        }
    }

}
