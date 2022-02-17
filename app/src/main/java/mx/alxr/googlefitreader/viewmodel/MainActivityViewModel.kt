package mx.alxr.googlefitreader.viewmodel

import androidx.annotation.ColorInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.fitness.HistoryClient
import mx.alxr.googlefitreader.R
import mx.alxr.googlefitreader.base.SingleEvent
import mx.alxr.googlefitreader.models.BloodPressureItem
import mx.alxr.googlefitreader.models.BloodPressureRecord
import mx.alxr.googlefitreader.models.MainModel
import mx.alxr.googlefitreader.models.PermissionRequestArguments
import mx.alxr.googlefitreader.repository.*
import mx.alxr.googlefitreader.utils.IResourcesWrapper
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.text.DecimalFormat
import javax.inject.Inject

const val TOP_SYSTOLIC = 120
const val TOP_DIASTOLIC = 80

class MainActivityViewModel
@Inject constructor(
    private val googleFitWrapper: IGoogleFitWrapper,
    private val resourcesWrapper: IResourcesWrapper
) : ViewModel(),
    Subscriber<List<BloodPressureRecord>> {

    private val fitnessApiStuff: FitnessApiStuff = googleFitWrapper.attach(this)
    private val formatter = DateTimeFormatter
        .ofPattern(resourcesWrapper.getString(R.string.date_pattern))
    private val numberFormat = DecimalFormat("#.#")

    private val bloodPressureRecordListLiveData: MutableLiveData<List<BloodPressureItem>> =
        MutableLiveData()

    private val mainModelMutableLiveData: MutableLiveData<MainModel> =
        MutableLiveData(getDefaultModel())

    init {
        println("MainActivityViewModel##CONSTRUCTOR")
        requestGoogleFitData()
    }

    override fun onCleared() {
        println("MainActivityViewModel##onCleared")
        super.onCleared()
        googleFitWrapper.detach(fitnessApiStuff)
    }

    override fun onNext(t: List<BloodPressureRecord>) {
        println("MainActivityViewModel##onNext " + t.size)
        bloodPressureRecordListLiveData.value = map(t)
        setModel(
            getCurrentModel().copy(
                swipeEnabled = true,
                swipeRefreshing = false,
                lastSyncDate = resourcesWrapper.getString(
                    R.string.last_google_fit_sync,
                    ZonedDateTime.now().format(formatter)
                )
            )
        )
    }

    override fun onError(throwable: Throwable) {
        println("MainActivityViewModel##onError $throwable")
        throwable.printStackTrace()
        setModel(
            getCurrentModel().copy(
                errorMessageEvent = SingleEvent("NO DATA $throwable"),
                swipeEnabled = true,
                swipeRefreshing = false
            )
        )
    }

    fun getMainModelLiveData(): LiveData<MainModel> {
        return mainModelMutableLiveData
    }

    fun getBloodPressureRecordListLiveData(): LiveData<List<BloodPressureItem>> {
        return bloodPressureRecordListLiveData
    }

    fun onDemoShown() {
        val model = getCurrentModel()
        setModel(
            model.copy(
                isDemoConsumed = true,
                isContentVisible = true
            )
        )
    }

    fun onEnableGoogleFitClicked() {
        println("MainActivityViewModel##onEnableGoogleFitClicked")
        val model = getCurrentModel()
        setModel(
            model.copy(
                permissionRequestViewVisible = true,
                permissionRequestEvent = SingleEvent(
                    PermissionRequestArguments(
                        fitnessApiStuff.fitnessOptions,
                        fitnessApiStuff.account
                    )
                )
            )
        )
    }

    fun onPermissionGranted(result: Boolean) {
        println("MainActivityViewModel##onPermissionGranted $result")
        if (result) {
            requestGoogleFitData()
        } else {
            setModel(
                getCurrentModel().copy(
                    errorMessageEvent = SingleEvent("NO PERMISSION. TRY AGAIN.")
                )
            )
        }
    }

    fun onHistoryClientCreated(historyClient: HistoryClient) {
        println("MainActivityViewModel##onHistoryClientCreated")
        val now = LocalDateTime.now()
        val model = getCurrentModel()
        try {
            googleFitWrapper.getRecords(
                RequestData(
                    fitnessApiStuff = fitnessApiStuff,
                    historyClient = historyClient,
                    startSeconds = now.minusDays(30).atZone(ZoneId.systemDefault()).toEpochSecond(),
                    endSeconds = now.atZone(ZoneId.systemDefault()).toEpochSecond()
                )
            )
            setModel(
                model.copy(
                    permissionRequestViewVisible = false
                )
            )
        } catch (e: MissingPermissionException) {
            println("MainActivityViewModel##onHistoryClientCreated $e")
            if (model.explicitPermissionRequestHandled) {
                setModel(
                    model.copy(
                        permissionRequestViewVisible = true
                    )
                )
            } else {
                setModel(
                    model.copy(
                        permissionRequestViewVisible = true,
                        permissionRequestEvent = SingleEvent(
                            PermissionRequestArguments(
                                fitnessApiStuff.fitnessOptions,
                                fitnessApiStuff.account
                            )
                        )
                    )
                )
            }
        }
    }

    fun onRefreshRequested() {
        println("MainActivityViewModel##onRefreshRequested")
        requestGoogleFitData()
    }

    private fun requestGoogleFitData() {
        println("MainActivityViewModel##requestGoogleFitData")
        setModel(getCurrentModel().copy(requestHistoryClientEvent = SingleEvent(fitnessApiStuff.account)))
    }

    private fun map(list: List<BloodPressureRecord>): List<BloodPressureItem> {
        val result = ArrayList<BloodPressureItem>(list.size)
        for (record in list) {
            result.add(record.map())
        }
        return result
    }

    private fun BloodPressureRecord.map(): BloodPressureItem {
        val zonedDateTime = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(timeStamp),
            ZoneId.systemDefault()
        )
        val isWarningShown = isNotSafe()
        return BloodPressureItem(
            dateLabel = zonedDateTime.format(formatter),
            isExclamationMarkVisible = isWarningShown,
            topLabel = resourcesWrapper.getString(
                R.string.label_systolic,
                numberFormat.format(systolic)
            ),
            bottomLabel = resourcesWrapper.getString(
                R.string.label_diastolic,
                numberFormat.format(diastolic)
            ),
            labelColor = isWarningShown.getLabelColor()
        )
    }

    /**
     * Normal to be Less than 120 Systolic and less than 80 Diastolic.
     */
    private fun BloodPressureRecord.isNotSafe(): Boolean {
        if (systolic > TOP_SYSTOLIC || diastolic > TOP_DIASTOLIC) {
            return true
        }
        return false
    }

    @ColorInt
    private fun Boolean.getLabelColor(): Int {
        return if (this) {
            resourcesWrapper.getColor(R.color.warning_label_color)
        } else {
            resourcesWrapper.getColor(R.color.normal_label_color)
        }
    }

    private fun getCurrentModel(): MainModel {
        val model = mainModelMutableLiveData.value
        return model ?: getDefaultModel()
    }

    private fun getDefaultModel(): MainModel {
        return MainModel(requestHistoryClientEvent = SingleEvent(fitnessApiStuff.account))
    }

    private fun setModel(model: MainModel) {
        mainModelMutableLiveData.value = model
    }

}