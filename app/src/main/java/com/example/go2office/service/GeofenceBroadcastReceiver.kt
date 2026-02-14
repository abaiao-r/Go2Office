package com.example.go2office.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.go2office.data.local.dao.OfficePresenceDao
import com.example.go2office.data.local.entities.OfficePresenceEntity
import com.example.go2office.domain.usecase.AggregateSessionsToDailyUseCase
import com.example.go2office.util.WorkHoursCalculator
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * BroadcastReceiver that handles geofence transition events.
 */
@AndroidEntryPoint
class GeofenceBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var officePresenceDao: OfficePresenceDao

    @Inject
    lateinit var notificationHelper: OfficeNotificationHelper

    @Inject
    lateinit var aggregateSessionsUseCase: AggregateSessionsToDailyUseCase

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent == null) {
            Log.e(TAG, "GeofencingEvent is null")
            return
        }

        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, "Geofencing error: $errorMessage")
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition

        when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                handleGeofenceEntry(context)
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                handleGeofenceExit(context)
            }
            else -> {
                Log.e(TAG, "Unknown geofence transition: $geofenceTransition")
            }
        }
    }

    private fun handleGeofenceEntry(context: Context) {
        Log.d(TAG, "Geofence ENTER detected")

        scope.launch {
            try {
                // Check if already have an active session
                val activeSession = officePresenceDao.getCurrentActiveSession()
                if (activeSession != null) {
                    Log.d(TAG, "Already have an active session, ignoring entry")
                    return@launch
                }

                // Create new session
                val session = OfficePresenceEntity(
                    entryTime = LocalDateTime.now(),
                    exitTime = null,
                    isAutoDetected = true,
                    confidence = 0.95f, // TODO: Calculate based on location accuracy
                    createdAt = Instant.now()
                )

                val sessionId = officePresenceDao.insert(session)
                Log.d(TAG, "Created new office presence session: $sessionId")

                // Show notification
                notificationHelper.showArrivalNotification(context)

            } catch (e: Exception) {
                Log.e(TAG, "Error handling geofence entry", e)
            }
        }
    }

    private fun handleGeofenceExit(context: Context) {
        Log.d(TAG, "Geofence EXIT detected")

        scope.launch {
            try {
                // Get current active session
                val activeSession = officePresenceDao.getCurrentActiveSession()
                if (activeSession == null) {
                    Log.d(TAG, "No active session found, ignoring exit")
                    return@launch
                }

                val exitTime = LocalDateTime.now()

                // Calculate session hours
                val hours = WorkHoursCalculator.calculateSessionHours(
                    activeSession.entryTime,
                    exitTime
                )

                // Check minimum duration (15 minutes = 0.25 hours)
                if (hours < 0.25f) {
                    Log.d(TAG, "Session too short (${hours}h), deleting as false positive")
                    officePresenceDao.delete(activeSession)
                    return@launch
                }

                // Update session with exit time
                val updatedSession = activeSession.copy(exitTime = exitTime)
                officePresenceDao.update(updatedSession)

                Log.d(TAG, "Updated session with exit time. Duration: ${hours}h")

                // Show notification
                notificationHelper.showDepartureNotification(context, hours)

                // Aggregate to daily entry
                aggregateToDailyEntry(activeSession.entryTime.toLocalDate())

            } catch (e: Exception) {
                Log.e(TAG, "Error handling geofence exit", e)
            }
        }
    }

    private suspend fun aggregateToDailyEntry(date: java.time.LocalDate) {
        try {
            val result = aggregateSessionsUseCase(date)
            if (result.isFailure) {
                Log.e(TAG, "Failed to aggregate sessions: ${result.exceptionOrNull()?.message}")
            } else {
                Log.d(TAG, "Successfully aggregated sessions for $date")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error aggregating to daily entry", e)
        }
    }

    companion object {
        private const val TAG = "GeofenceBroadcastReceiver"
    }
}

