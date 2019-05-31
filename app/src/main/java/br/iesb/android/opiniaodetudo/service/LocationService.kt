package br.iesb.android.opiniaodetudo.service

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.content.ContextCompat

class LocationService(val context: Context) {
    fun onLocationObtained(callback: (Double, Double) -> Unit) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED){
            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location?) {
                    callback(location!!.latitude, location!!.longitude)
                    locationManager.removeUpdates(this)
                }
                override fun onStatusChanged(
                    provider: String?,
                    status: Int,
                    extras: Bundle?) {
                }
                override fun onProviderEnabled(provider: String?) {
                }
                override fun onProviderDisabled(provider: String?) {
                }
            }
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 0, 0f, listener)
        }
    }
}