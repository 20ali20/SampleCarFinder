package com.alimojarrad.fair.Modules.Selection

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.SeekBar
import com.alimojarrad.fair.Modules.Main.MainActivity
import com.alimojarrad.fair.R
import com.alimojarrad.fair.Services.API.Interfaces.CarResultQueryParam
import com.google.android.gms.maps.model.LatLng
import com.sidecarhealth.Modules.Common.Util
import kotlinx.android.synthetic.main.activity_find_car.*
import java.io.IOException
import java.io.Serializable
import java.text.SimpleDateFormat


class FindCarActivity : AppCompatActivity() {
    enum class NavigationType : Serializable {
        EXPECTSRESULTS,
        NORESULTS
    }

    companion object {

        const val dataKey = "FindCarDataKey"
        const val resultCode = 999
        const val membersKey = "MEMBERSKEY"
        const val navigationKey = "NAVIGATION"

        fun startActivityForResult(activity: Activity) {
            val intent = Intent(activity, FindCarActivity::class.java)
            var bundle = Bundle()
            bundle.putSerializable(navigationKey, NavigationType.EXPECTSRESULTS)
            intent.putExtras(bundle)
            activity.startActivityForResult(intent, resultCode)
        }

        fun startActivity(context: Context) {
            val intent = Intent(context, FindCarActivity::class.java)
            var bundle = Bundle()
            bundle.putSerializable(navigationKey, NavigationType.NORESULTS)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }


   private var map = HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_car)
        setupViews()
        setupInteraction()
    }

    private fun retrieveNavigationType(): NavigationType? {
        return intent.getSerializableExtra(navigationKey) as? NavigationType
    }

    private fun setupViews(){
        findcaractivity_radius_amount.text = "${findcaractivity_radius.progress}"
    }

    private fun setupInteraction() {

        findcaractivity_radius.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(_p0: SeekBar?, progress: Int, _p2: Boolean) {
                findcaractivity_radius_amount.text = "$progress"
            }
            override fun onStartTrackingTouch(_p0: SeekBar?) {}
            override fun onStopTrackingTouch(_p0: SeekBar?) {}
        })

        findcaractivity_viewoptions.setOnClickListener {
            retrieveNavigationType()?.let {
                when (it) {
                    NavigationType.EXPECTSRESULTS -> {
                        if (validateAndBuildQuery()) {
                            proceedWithResults(map)
                        }
                    }
                    NavigationType.NORESULTS -> {
                        if (validateAndBuildQuery()) {
                            proceed(map)
                        }
                    }
                }
            }
        }
    }

    private fun validateAndBuildQuery(): Boolean {
        var flag = true
        if (findcaractivity_address.text.isEmpty()) {
            flag = false
            findcaractivity_address.error = "Please enter an address first"
        } else {
            findcaractivity_address.error = null
            var latlng = getGeoLocation(findcaractivity_address.text)
            if (latlng != null) {
                map[CarResultQueryParam.Latitude.q] = Util.getRoundedDouble(latlng!!.latitude).toString()
                map[CarResultQueryParam.Longitutde.q] = Util.getRoundedDouble(latlng!!.longitude).toString()
                map[CarResultQueryParam.Radius.q] = "50"
            } else {
                flag = false
                findcaractivity_address.error = "Please enter a valid address"
            }
        }

        if (findcaractivity_pickup.text.isEmpty()) {
            flag = false
            findcaractivity_pickup.error = "Please enter a pickup date first"
        } else if (findcaractivity_pickup.text.length != 10) {
            flag = false
            findcaractivity_pickup.error = "Please enter a pickup date first"
        } else {
            findcaractivity_pickup.error = null
            var date = findcaractivity_pickup.text
            map[CarResultQueryParam.PickupTime.q] = date
        }

        if (findcaractivity_dropoff.text.isEmpty()) {
            flag = false
            findcaractivity_dropoff.error = "Please enter a drop-off date first"
        } else if (findcaractivity_dropoff.text.length != 10) {
            flag = false
            findcaractivity_dropoff.error = "Please enter a pickup date first"
        } else {
            findcaractivity_dropoff.error = null
            var isDropOffAfterPickup = isFirstDateBigger(findcaractivity_dropoff.text,findcaractivity_pickup.text)
            if (isDropOffAfterPickup) {
                map[CarResultQueryParam.DropOff.q] = findcaractivity_dropoff.text
            } else {
                flag = false
                findcaractivity_dropoff.error = "Drop-off date must be after pickup."
            }
        }
        if(flag){
            map[CarResultQueryParam.Radius.q]=findcaractivity_radius_amount.text.toString()
        }
        return flag
    }

    private fun proceedWithResults(map: HashMap<String, String>) {
        var intent = Intent()
        intent.putExtra(dataKey,map)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun proceed(map: HashMap<String, String>) {
        MainActivity.startActivity(this,map)
    }

    private fun getGeoLocation(strAddress: String): LatLng? {
        val coder = Geocoder(this)
        val address: List<Address>?
        var p1: LatLng? = null
        try {
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null) {
                return null
            }
            val location = address[0]
            p1 = LatLng(location.latitude, location.longitude)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }catch (ob : IndexOutOfBoundsException){
            ob.printStackTrace()
        }
        return p1
    }

    private fun isFirstDateBigger(firstDate: String, secondDate: String): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val date1 = sdf.parse(firstDate)
        val date2 = sdf.parse(secondDate)
        return date1.after(date2)
    }



}
