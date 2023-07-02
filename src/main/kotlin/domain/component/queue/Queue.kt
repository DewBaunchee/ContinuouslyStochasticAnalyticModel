package domain.component.queue

import domain.request.Request


class Queue {

    private var current: Request? = null

    fun enqueue(request: Request?): Boolean {
        if (request == null) throw Exception()
        if (isFull()) {
            if (current!!.priority >= request.priority) return false
            current!!.repressed = true
        }
        current = request
        return true
    }

    fun dequeue(): Request? {
        return current?.also { current = null }
    }

    fun peek(): Request? {
        return current
    }

    fun isFull(): Boolean {
        return current != null
    }
}