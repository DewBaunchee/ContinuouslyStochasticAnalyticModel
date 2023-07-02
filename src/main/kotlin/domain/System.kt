package domain

import domain.component.channel.Channels
import domain.component.queue.Queue
import domain.component.source.PoissonSource
import domain.request.Request
import java.util.*
import kotlin.math.min

class System(
    private val source: PoissonSource,
    private val queue: Queue,
    private val channels: Channels
) {

    private fun count(type: Int): Int {
        return queue.peek().let { if (it == null || it.type != type) 0 else 1 } +
            channels.list.count { it.request?.type == type }
    }

    private val state get() = State(count(1), count(2))

    private fun tick(): Triple<State, Request?, Double> {
        val deltaT = min(channels.minimalTime, source.remainTime)
        channels.tick(deltaT)
        queue.dequeue()?.also {
            if (!channels.handle(it)) queue.enqueue(it)
        }

        val emitted = source.tick(deltaT)
        if (emitted != null) {
            if (!channels.handle(emitted) && !queue.enqueue(emitted)) {
                emitted.refusedOnSource = true
            }
        }

        return Triple(state, emitted, deltaT)
    }

    fun simulate(tickCount: Int): Statistics {
        val requests = mutableListOf<Request>()
        val states = mutableMapOf<State, Double>()
        for (i in 0 until tickCount) {
            val tickResult = tick()

            states.compute(tickResult.first) { _, count ->
                if (count == null) return@compute tickResult.third
                return@compute count + tickResult.third
            }

            if (tickResult.second != null) requests.add(tickResult.second!!)
        }

        return Statistics.from(states, requests.toList())
    }

    class State(val firstType: Int, val secondType: Int) {

        override fun hashCode(): Int {
            return Objects.hash(firstType, secondType)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as State

            if (firstType != other.firstType) return false
            if (secondType != other.secondType) return false

            return true
        }

        override fun toString(): String {
            return "$firstType$secondType"
        }
    }
}