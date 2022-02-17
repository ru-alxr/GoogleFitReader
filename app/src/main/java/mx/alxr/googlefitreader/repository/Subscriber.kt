package mx.alxr.googlefitreader.repository

interface Subscriber<T> {
    fun onNext(t: T)
    fun onError(throwable: Throwable)
}