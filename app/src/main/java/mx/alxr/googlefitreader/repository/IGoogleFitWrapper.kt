package mx.alxr.googlefitreader.repository

import mx.alxr.googlefitreader.models.BloodPressureRecord

interface IGoogleFitWrapper {

    fun attach(subscriber: Subscriber<List<BloodPressureRecord>>): FitnessApiStuff

    fun detach(fitnessApiStuff: FitnessApiStuff)

    @Throws(MissingPermissionException::class)
    fun getRecords(
        requestData: RequestData
    )

}