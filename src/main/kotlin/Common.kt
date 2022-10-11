import domain.Statistics
import domain.component.channel.Channels
import domain.component.queue.Queue
import domain.impl.ExponentialChannel
import domain.impl.PoissonSource
import domain.request.Request
import kotlin.random.Random

const val tickCount = 1_000_000
const val deltaT = 1.0

fun firstType(): Request {
    return Request(2)
}

fun secondType(): Request {
    return Request(1)
}

fun name(key: String): String {
    return when (key) {
        "000" -> "1"
        "010" -> "2"
        "020" -> "3"
        "110" -> "4"
        "210" -> "5"
        "120" -> "6"

        "001" -> "7"
        "002" -> "8"

        "011" -> "9"
        "012" -> "10"

        "021" -> "11"
        "022" -> "12"

        "111" -> "13"
        "112" -> "14"

        "121" -> "15"
        "122" -> "16"

        "211" -> "17"
        "212" -> "18"

        "221" -> "19"
        "222" -> "20"

        else -> "UNKNOWN"
    }
}

fun getSystem(): domain.System {
    val mu = 0.5
    val lambda = 0.9
    val p = 0.5

    val source = PoissonSource(lambda) { if (Random.nextDouble() > p) secondType() else firstType() }
    val queue = Queue(1)
    val channels = Channels(false, queue, (0 until 2).map { ExponentialChannel(it, mu) })
    return domain.System(source, queue, channels)
}

fun print(statistics: Statistics) {
    println()
    println("================================================== STATS ==================================================")
    println()

    statistics.apply {
        val printTable = listOf(
            statesProbability.entries.map {
                "P-${it.key} (${name(it.key.toString())}) = ${it.value}"
            },
            listOf(
                "Refuse probability             (Potk)  = $refuseProbability",
                "Block probability              (Pbl)   = $blockProbability",
                "Average queue length           (Loch)  = $averageQueueLength",
                "Average request count          (Lc)    = $averageRequestCount",
                "Relative throughput            (Q)     = $relativeThroughput",
                "Absolute throughput            (A)     = $absoluteThroughput",
                "Average time in queue          (Woch)  = $averageTimeInQueue",
                "Average time in system         (Wc)    = $averageTimeInSystem",
            ).plus(
                channelCoefficients.mapIndexed { index, value ->
                    "Channel ${index + 1} coefficient          (Kkan${index + 1}) = $value"
                }
            ).plus(
                channelRelativeThroughput.mapIndexed { index, value ->
                    "Channel ${index + 1} relative throughput  (Q${index + 1})    = $value"
                }
            )
        )

        val max = printTable.maxOf { it.size }
        for (i in 0 until max) {
            println(
                String.format(
                    "%50s   |   %s",
                    args = Array(printTable.size) { printTable[it].getOrElse(i) { "" } })
            )
        }
    }
}