package domain.component.channel

import domain.request.Request
import timeExp


class ExponentialChannel(private val mu: Double)  {

    var request: Request? = null
        private set

    var remainTime = Double.POSITIVE_INFINITY
        private set

    fun tick(deltaT: Double): Request? {
        if (request == null) return null

        remainTime -= deltaT
        if (remainTime > 0) return null
        remainTime = Double.POSITIVE_INFINITY
        return request?.also { request = null }
    }

    fun handle(request: Request): Request? {
        remainTime = timeExp(mu)
        return this.request.also { this.request = request }
    }

    fun isFree(): Boolean {
        return request == null
    }
}