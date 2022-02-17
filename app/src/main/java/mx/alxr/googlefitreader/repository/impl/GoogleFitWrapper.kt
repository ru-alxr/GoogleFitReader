package mx.alxr.googlefitreader.repository.impl

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.HistoryClient
import com.google.android.gms.fitness.data.DataPoint
import com.google.android.gms.fitness.data.HealthDataTypes
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.request.DataUpdateListenerRegistrationRequest
import com.google.android.gms.fitness.result.DataReadResponse
import mx.alxr.googlefitreader.models.BloodPressureRecord
import mx.alxr.googlefitreader.repository.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GoogleFitWrapper @Inject constructor(private val context: Context) : IGoogleFitWrapper {

    private val dataType =
        HealthDataTypes.TYPE_BLOOD_PRESSURE// DataType.TYPE_HEART_RATE_BPM OR HealthDataTypes.TYPE_BLOOD_PRESSURE

    private var subscriber: Subscriber<List<BloodPressureRecord>>? = null
    private var historyClient: HistoryClient? = null

    override fun attach(subscriber: Subscriber<List<BloodPressureRecord>>): FitnessApiStuff {
        this.subscriber = subscriber
        val fitnessOptions: FitnessOptions = FitnessOptions
            .builder()
            .addDataType(dataType, FitnessOptions.ACCESS_READ)
            .build()
        val googleSignInAccount = GoogleSignIn.getAccountForExtension(context, fitnessOptions)
        val intent = Intent(context, GoogleFitUpdateIntentService::class.java)
        val pendingIntent =
            PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        return FitnessApiStuff(
            fitnessOptions,
            googleSignInAccount,
            pendingIntent,
            dataType
        )
    }

    override fun detach(fitnessApiStuff: FitnessApiStuff) {
        this.subscriber = null
        this.historyClient?.unregisterDataUpdateListener(fitnessApiStuff.pendingIntent)
        this.historyClient = null
    }

    override fun getRecords(requestData: RequestData) {
        this.historyClient = requestData.historyClient
        if (subscriber == null) {
            throw IllegalStateException("Missing subscriber")
        }
        val hasPermission = GoogleSignIn.hasPermissions(
            requestData.fitnessApiStuff.account,
            requestData.fitnessApiStuff.fitnessOptions
        )
        if (!hasPermission) {
            throw MissingPermissionException()
        }
        val request = DataUpdateListenerRegistrationRequest
            .Builder()
            .setDataType(requestData.fitnessApiStuff.dataType)
            .setPendingIntent(requestData.fitnessApiStuff.pendingIntent)
            .build()
        requestData.historyClient.registerDataUpdateListener(request)
            .addOnSuccessListener {
                println("GoogleFitWrapper##getRecords registerDataUpdateListener success")
            }
            .addOnFailureListener {
                // TODO NO IDEA WHAT'S WRONG... NEED TO INVESTIGATE
                // TODO UPDATE: THIS APPROACH IS NOT WORKING see https://developers.google.com/android/reference/com/google/android/gms/fitness/HistoryClient#public-taskvoid-registerdataupdatelistener-dataupdatelistenerregistrationrequest-request
                println("GoogleFitWrapper##getRecords registerDataUpdateListener fail")
                it.printStackTrace()
            }
        val readRequest = DataReadRequest
            .Builder()
            .enableServerQueries()
            .setTimeRange(
                requestData.startSeconds,
                requestData.endSeconds,
                TimeUnit.SECONDS
            )
            .read(requestData.fitnessApiStuff.dataType)
            .build()
        requestData.historyClient
            .readData(readRequest)
            .addOnSuccessListener { result ->
                try {
                    subscriber?.onNext(consume(result))
                } catch (e: Exception) {
                    subscriber?.onError(e)
                }
            }
            .addOnFailureListener { subscriber?.onError(it) }
    }

    @Throws
    private fun consume(response: DataReadResponse): List<BloodPressureRecord> {
        println("GoogleFitWrapper##consume...")
        val ds = response.getDataSet(dataType)
        val list = ArrayList<BloodPressureRecord>()
        for (dp in ds.dataPoints) {
            try {
                list.add(dp.getRecord())
            } catch (e: NullPointerException) {
                continue
            }
        }
        return list
    }

    @Throws(NullPointerException::class)
    private fun DataPoint.getRecord(): BloodPressureRecord {
        return BloodPressureRecord(
            getTimeStamp(),
            getBloodPressureSystolic(),
            getBloodPressureDiastolic()
        )
    }

    private fun DataPoint.getBloodPressureSystolic(): Float {
        for (field in dataType.fields) {
            if ("blood_pressure_systolic(f)" == field.toString()) {
                return getValue(field).asFloat()
            }
        }
        throw NullPointerException()
    }

    private fun DataPoint.getBloodPressureDiastolic(): Float {
        for (field in dataType.fields) {
            if ("blood_pressure_diastolic(f)" == field.toString()) {
                return getValue(field).asFloat()
            }
        }
        throw NullPointerException()
    }

    private fun DataPoint.getTimeStamp(): Long {
        return getTimestamp(TimeUnit.MILLISECONDS)
    }

}