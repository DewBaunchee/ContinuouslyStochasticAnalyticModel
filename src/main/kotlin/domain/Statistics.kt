package domain

import domain.request.Request

data class Statistics(
    val states: Map<System.State, Double>,
    val q1: Double, // Q1
    val q2: Double, // Q2
) {

    companion object {

        fun from(states: Map<System.State, Double>, requests: List<Request>): Statistics {
            val allTime = states.values.sum()
            return Statistics(
                states.mapValues { it.value / allTime },
                q(requests, 1),
                q(requests, 2)
            )
        }

        private fun q(requests: List<Request>, type: Int): Double {
            val typed = requests.filter { it.type == type }
            val refused = typed.filter { it.isRefused() }
            val handled = typed.filter { it.handled }

            return handled.size.toDouble() / (refused.size + handled.size)
        }
    }
}