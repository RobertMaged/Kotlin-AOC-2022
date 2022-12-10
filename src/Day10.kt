import java.util.Collections

fun main() {
    fun part1(input: List<String>): Int {
        var products = mutableListOf<Int>()
        val x = listOf(1) + input.flatMap { line ->
            val ins = line.takeWhile { it.isLetter() }

            if (ins == "noop") {
                listOf(0)
            } else {

                val n = line.dropWhile { (it != '-' && it.isLetter()) || it == ' ' }

                listOf(0, n.toInt())
            }
        }.foldIndexed(1) { index, acc, add ->
            if (index + 1 in listOf(20, 60, 100, 140, 180, 220))
                products.add((index + 1) * acc)

            acc + add
        }


        return products.sum()
    }

    fun part2(input: List<String>): Int {
        val spiritPosititon = List(40) { if (it < 3) '#' else '.' }.toMutableList()

/*        for (line in input){
            val ins = line.takeWhile { it.isLetter() }
            var curr = 0
            if (ins == "noop") {
//                listOf(0)
            } else {

                val n = line.dropWhile { (it != '-' && it.isLetter()) || it == ' ' }

                curr = n.toInt()
            }
        }*/

        input.flatMap { line ->
            val ins = line.takeWhile { it.isLetter() }

            if (ins == "noop") {
                listOf(0)
            } else {

                val n = line.dropWhile { (it != '-' && it.isLetter()) || it == ' ' }

                listOf(0, n.toInt())
            }
        }.foldIndexed(1) { index, acc, add ->
            require(spiritPosititon.size == 40)

            val pixel = (index ) % 40
            print(spiritPosititon[pixel])
            if ((index + 1) % 40 == 0) {
                println()
            }
            Collections.rotate(spiritPosititon, add )
//println(spiritPosititon.joinToString(separator = ""))
            acc + add
        }
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
//    check(part1(testInput).also(::println) == 13140)
//    check(part2(testInput).also(::println) == 1)

    val input = readInput("Day10")
//    println(part1(input))
//    println(part2(input))
    part2(input)
}
