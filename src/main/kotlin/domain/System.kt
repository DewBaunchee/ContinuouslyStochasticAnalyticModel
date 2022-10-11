package domain

import domain.component.channel.Channels
import domain.component.queue.Queue
import domain.component.source.Source
import domain.request.Request
import java.util.*

class System(
    private val source: Source,
    private val queue: Queue,
    private val channels: Channels
) {

    var totalTime = 0.0
        private set

    private val state get() = State(queue.state, channels.state)

    private fun tick(deltaT: Double): Pair<State, Request?> {
        totalTime += deltaT
        channels.tick(deltaT)
        queue.tick(deltaT)
        if (channels.handle(queue.peek())) queue.dequeue()

        val emitted = source.tick(deltaT)
        if (emitted != null) {
            if (!channels.handle(emitted) && !queue.enqueue(emitted)) {
                emitted.refusedOnSource = true
            }
        }

        return state to emitted
    }

    fun simulate(tickCount: Int, deltaT: Double): Statistics {
        val requests = mutableListOf<Request>()
        val states = mutableMapOf<State, Int>()
        for (i in 0 until tickCount) {
            val tickResult = tick(deltaT)

            states.compute(tickResult.first) { _, count ->
                if (count == null) return@compute 1
                return@compute count + 1
            }

            if (tickResult.second != null) requests.add(tickResult.second!!)
        }

        return Statistics.from(tickCount, channels, requests.toList(), states.toMap())
    }

    class State(
        val queueSize: Int,
        val channelBusiness: List<Int>
    ) {

        val requestCount get() = queueSize + channelBusiness.sumOf { (if (it != 0) 1 else 0).toInt() }

        override fun hashCode(): Int {
            return Objects.hash(queueSize, channelBusiness)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as State

            if (queueSize != other.queueSize) return false
            if (channelBusiness != other.channelBusiness) return false

            return true
        }

        override fun toString(): String {
            return "" + queueSize + channelBusiness.joinToString("") { it.toString() }
        }
    }
}