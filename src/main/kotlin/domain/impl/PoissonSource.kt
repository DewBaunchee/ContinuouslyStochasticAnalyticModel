package domain.impl

import domain.component.source.Source
import domain.request.Request
import java.util.function.Supplier
import kotlin.random.Random

class PoissonSource(
    val threshold: Double,
    val requestFactory: Supplier<Request>
) : Source {

    override val state: String get() = ""

    override fun tick(deltaT: Double): Request? {
        return if (Random.nextDouble() > threshold) requestFactory.get() else null
    }
}