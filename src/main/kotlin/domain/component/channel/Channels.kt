package domain.component.channel

import domain.component.Component
import domain.component.queue.Queue
import domain.request.Request

class Channels(
    private val parallel: Boolean,
    private val queue: Queue,
    val list: List<Channel>
) : Component<List<Int>> {

    override val state: List<Int>
        get() {
            return list.map { it.state }
        }

    fun tick(deltaT: Double) {
        if (parallel) {
            list.forEach { it.tick()?.handled = true }
        } else {
            var next = list.last()

            next.tick()?.let {
                it.handled = true
            }
            for (i in (list.size - 2) downTo 0) {
                val current = list[i]

                current.tick()?.also {
                    if (next.isFree()) {
                        next.handle(it)
                        return@also
                    }
                    if (next.request!!.priority >= it.priority) {
                        it.refusedOnChannel = current
                        return@also
                    }

                    val repressed = next.handle(it)!!
                    if (!queue.enqueue(repressed)) repressed.repressed = true
                }

                next = current
            }
        }
    }

    fun handle(request: Request?): Boolean {
        if (request == null) return false

        (if (parallel) {
            list.find { it.isFree() } ?: list.find { it.request!!.priority < request.priority }
        } else {
            if (list[0].isFree() || list[0].request!!.priority < request.priority) list[0] else null
        })
            ?.handle(request)
            ?.also {
                if (!queue.enqueue(it)) it.repressed = true
                return true
            }
        return false
    }
}