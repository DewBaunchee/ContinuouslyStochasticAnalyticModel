import java.util.stream.IntStream

fun main() {
    IntStream.range(0, 50)
        .parallel()
        .mapToObj { getSystem().simulate(tickCount, deltaT) }
        .sequential()
        .forEach { print(it) }
}