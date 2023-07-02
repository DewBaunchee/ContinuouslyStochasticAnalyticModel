package domain.request

class Request(val priority: Int, val type: Int) {

    var handled = false
    var refusedOnSource = false
    var repressed = false

    fun isRefused(): Boolean {
        return refusedOnSource || repressed
    }
}