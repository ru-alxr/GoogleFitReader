package mx.alxr.googlefitreader.base

class SingleEvent<T>(private val t: T) {

    private var received: Boolean = false

    fun getValue(): T? {
        if (received) {
            return null
        }
        received = true
        return t
    }

}