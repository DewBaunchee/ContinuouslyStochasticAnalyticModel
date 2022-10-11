package domain.component.channel

import domain.component.Component
import domain.request.Request


abstract class Channel(private val index: Int) : Component<Int> {

    val requests = mutableListOf<Request>()

    var request: Request? = null
        protected set

    override val state: Int get() = if (request == null) 0 else request!!.priority

    var busyTicks = 0
        private set

    fun tick(): Request? {
        if (isBusy()) {
            busyTicks++
            request!!.tickInChannel(index)
        }
        return internalTick()
    }

    fun handle(request: Request?): Request? {
        if (request != null) requests.add(request)
        return this.request.also { this.request = request }
    }

    fun isFree(): Boolean {
        return request == null
    }

    fun isBusy(): Boolean {
        return !isFree()
    }

    fun relativeThroughput(): Double {
        return requests.count { it.handled } / requests.size.toDouble()
    }

    protected abstract fun internalTick(): Request?
}