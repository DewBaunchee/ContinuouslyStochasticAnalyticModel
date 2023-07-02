package domain.component.source

import domain.request.Request
import timeExp
import timePoisson
import java.util.function.Supplier

class PoissonSource(
    private val lambda: Double,
    private val requestFactory: Supplier<Request>
) {

    var remainTime = 0.0

    init {
        update()
    }

    fun tick(deltaT: Double): Request? {
        remainTime -= deltaT
        if (remainTime > 0) return null
        update()
        return requestFactory.get()
    }

    private fun update() {
        remainTime = timeExp(lambda)
    }
}