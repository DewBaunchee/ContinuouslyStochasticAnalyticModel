package domain.impl

import domain.component.channel.Channel
import domain.request.Request
import kotlin.random.Random

class ExponentialChannel(index: Int, private val threshold: Double) : Channel(index) {

    override fun internalTick(): Request? {
        return (if (Random.nextDouble() > threshold) request else null)?.also { request = null }
    }
}