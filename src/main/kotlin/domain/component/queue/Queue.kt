package domain.component.queue

import domain.component.Component
import domain.request.Request
import java.util.*


class Queue(private val maxCapacity: Int): Component<Int> {

    private val list = LinkedList<Request>()

    val size get() = list.size

    fun enqueue(request: Request?): Boolean {
        if (request == null) throw Exception()
        if (isFull()) return false
        list.add(request)
        return true
    }

    fun dequeue(): Request? {
        return list.pollFirst()
    }

    fun peek(): Request? {
        return list.peekFirst()
    }

    fun tick(deltaT: Double) {
        list.forEach { it.ticksInQueue++ }
    }

    private fun isFull(): Boolean {
        return list.size >= maxCapacity
    }

    override val state: Int
        get() {
            return list.size
        }
}