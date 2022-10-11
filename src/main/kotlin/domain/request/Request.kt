package domain.request

import domain.component.channel.Channel

class Request(val priority: Int) {

    var handled = false
    var refusedOnSource = false
    var refusedOnChannel: Channel? = null
    var repressed = false

    var ticksInQueue = 0

    private val mutableTicksInChannels = mutableMapOf<Int, Int>()

    val ticksInSystem get() = ticksInQueue + mutableTicksInChannels.entries.sumOf { it.value }

    fun isRefused(): Boolean {
        return refusedOnSource || refusedOnChannel != null
    }

    fun tickInChannel(channelIndex: Int) {
        mutableTicksInChannels.compute(channelIndex) { _, ticks ->
            if (ticks == null) return@compute 1
            return@compute ticks + 1
        }
    }
}