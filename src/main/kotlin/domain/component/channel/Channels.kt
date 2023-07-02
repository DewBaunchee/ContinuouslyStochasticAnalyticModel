package domain.component.channel

import domain.component.queue.Queue
import domain.request.Request

class Channels(
    private val queue: Queue,
    val list: List<ExponentialChannel>
) {

    val minimalTime get() = list.minOf { it.remainTime }

    fun tick(deltaT: Double) {
        list.forEach { it.tick(deltaT)?.handled = true }
    }

    fun handle(request: Request): Boolean {
        (list.find { it.isFree() } ?: list.find { it.request!!.priority < request.priority })
            .also { if (it == null) return false }!!
            .handle(request)
            .also { repressed ->
                if (repressed != null && !queue.enqueue(repressed)) repressed.repressed = true
                return true
            }
    }
}