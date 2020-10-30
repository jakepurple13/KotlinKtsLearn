package com.programmersbox.kotlinktslearn

import kotlin.math.floor
import kotlin.math.sqrt

class Sudoku {
    val gameBoard = Array(3) { Array(3) { Box() } }

}

class Box {
    val square = Array(3) { Array(3) { Cell() } }
}

class Cell(var num: Int? = null)

class InvalidSizeError : Exception("Requested size is invalid (4-n)")
class InvalidLevelError : Exception("Level isnt in range 0-3")

class Generator(private val size: Int, private val level: Int) {
    private var sqrSize: Int
    private val board: Array<Array<Int>>
    val sudoku = Sudoku()

    init {
        if (size < 4) throw InvalidSizeError()
        if (level !in 0..3) throw InvalidLevelError()

        sqrSize = sqrt(size.toDouble()).toInt()
        board = Array(size) { Array(size) { 0 } }
    }

    fun build(): Array<Array<Int>> {
        fillDiagonal()
        fillRemaining()
        setDifficulty()

        for (boxX in 0 until 3) {
            for (boxY in 0 until 3) {
                for (x in 0 until 3) {
                    for (y in 0 until 3) {
                        sudoku.gameBoard[boxX][boxY].square[x][y].num = board[boxX + x][boxY + y].let { if (it == 0) null else it }
                    }
                }
            }
        }

        return board
    }

    fun print() {
        for (i in 0 until size) {
            for (j in 0 until size)
                print("${board[i][j]} ")
            println()
        }
        println()
    }

    private fun fillDiagonal() {
        for (i in 0 until size step sqrSize) {
            fillBox(i, i)
        }
    }

    private fun fillBox(row: Int, col: Int) {
        var num: Int
        for (i in 0 until sqrSize) {
            for (j in 0 until sqrSize) {
                do {
                    num = randomNum(size)
                } while (!canFillBox(row, col, num))

                board[row + i][col + j] = num
            }
        }
    }

    private fun canFillBox(rowStart: Int, colStart: Int, num: Int): Boolean {
        for (i in 0 until sqrSize) {
            for (j in 0 until sqrSize) {
                if (board[rowStart + i][colStart + j] == num) return false
            }
        }

        return true
    }

    private fun randomNum(max: Int): Int {
        return floor((Math.random() * max + 1)).toInt()
    }

    private fun canFill(i: Int, j: Int, num: Int): Boolean {
        return canFillRow(i, num) &&
                canFillCol(j, num) &&
                canFillBox(i - i % sqrSize, j - j % sqrSize, num)
    }

    private fun canFillRow(i: Int, num: Int): Boolean {
        for (j in 0 until size)
            if (board[i][j] == num)
                return false
        return true
    }

    private fun canFillCol(j: Int, num: Int): Boolean {
        for (i in 0 until size)
            if (board[i][j] == num)
                return false
        return true
    }

    private fun fillRemaining(argI: Int = 0, argJ: Int = sqrSize): Boolean {
        var i = argI
        var j = argJ

        if (j >= size && i < size - 1) {
            i += 1
            j = 0
        }
        if (i >= size && j >= size)
            return true

        if (i < sqrSize) {
            if (j < sqrSize)
                j = sqrSize
        } else if (i < size - sqrSize) {
            if (j == (i / sqrSize) * sqrSize)
                j += sqrSize
        } else {
            if (j == size - sqrSize) {
                i += 1
                j = 0
                if (i >= size)
                    return true
            }
        }

        (1..size).shuffled().forEach { num ->
            if (canFill(i, j, num)) {
                board[i][j] = num
                if (fillRemaining(i, j + 1))
                    return true

                board[i][j] = 0
            }
        }
        return false
    }

    private fun setDifficulty() {
        var count = level + 5
        while (count != 0) {
            val cellId = randomNum(size * size) - 1
            val i = cellId / size
            val j = cellId % 9

            if (board[i][j] != 0) {
                count--
                board[i][j] = 0
            }
        }
    }
}