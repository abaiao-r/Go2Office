package com.example.go2office.service
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.go2office.data.local.entities.OfficeLocationEntity
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class GeofencingManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val geofencingClient = LocationServices.getGeofencingClient(context)
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }
    fun startGeofencing(location: OfficeLocationEntity, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val geofence = createGeofence(location)
        val geofencingRequest = createGeofencingRequest(geofence)
        try {
            geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
                addOnSuccessListener {
                    onSuccess()
                }
                addOnFailureListener { exception ->
                    onFailure(exception)
                }
            }
        } catch (securityException: SecurityException) {
            onFailure(securityException)
        }
    }
    fun stopGeofencing(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        geofencingClient.removeGeofences(geofencePendingIntent).run {
            addOnSuccessListener {
                onSuccess()
            }
            addOnFailureListener { exception ->
                onFailure(exception)
            }
        }
    }
    private fun createGeofence(location: OfficeLocationEntity): Geofence {
        return Geofence.Builder()
            .setRequestId(location.id.toString())
            .setCircularRegion(
                location.latitude,
                location.longitude,
                location.radiusMeters
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(
                Geofence.GEOFENCE_TRANSITION_ENTER or
                Geofence.GEOFENCE_TRANSITION_EXIT
            )
            .build()
    }
    private fun createGeofencingRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofence(geofence)
        }.build()
    }
}
