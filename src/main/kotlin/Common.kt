import domain.Statistics
import domain.System
import domain.component.channel.Channels
import domain.component.channel.ExponentialChannel
import domain.component.queue.Queue
import domain.component.source.PoissonSource
import domain.request.Request
import kotlin.math.exp
import kotlin.math.ln
import kotlin.random.Random

const val tickCount = 1_000_000

fun firstType(): Request {
    return Request(2, 1)
}

fun secondType(): Request {
    return Request(1, 2)
}

fun timeExp(factor: Double): Double {
//    val max = 1_000_000
//    val min = 0
//    val random = (Random.nextInt(max - min + 1) + min).toDouble() / (max - min)
    return (-1 / factor) * ln(Random.nextDouble())
}

fun timePoisson(factor: Double): Double {
    val l = exp(-factor)
    var k = 0
    var p = 1.0
    do {
        p *= Random.nextDouble()
        k++
    } while (p > l)
    return (k - 1).toDouble()
}

fun getSystem(): System {
    val mu = 0.5
    val lambda = 0.9
    val p = 0.5

    val source = PoissonSource(lambda) { if (Random.nextDouble() > p) secondType() else firstType() }
    val queue = Queue()
    val channels = Channels(queue, (0 until 2).map { ExponentialChannel(mu) })
    return System(source, queue, channels)
}

fun print(statistics: Statistics) {
    statistics.apply {
        println("Q1 = $q1")
        println("Q2 = $q2")

        states.entries.forEach {
            println("${name(it.key)} = ${it.value}")
        }
    }
}

private fun name(state: System.State): String {
    return state.toString().let {
        "P${
            when(it) {
                "00" -> 1
                "10" -> 2
                "20" -> 3
                "30" -> 4
                "01" -> 5
                "11" -> 6
                "21" -> 7
                "02" -> 8
                "12" -> 9
                "03" -> 10
                else -> "UNKNOWN"
            }
        } ($it)"
    }
}