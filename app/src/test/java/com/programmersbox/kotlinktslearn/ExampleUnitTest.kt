package com.programmersbox.kotlinktslearn

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val puzzle = Generator(9, 0)
        puzzle.build()
        puzzle.print()

        puzzle.sudoku.gameBoard.let {
            for (i in 0 until it.size) {
                for (j in 0 until it.size)
                    print(it[i][j].square.joinToString { it.joinToString { "${it.num}" } })
                println()
            }
            println()
        }
    }
}