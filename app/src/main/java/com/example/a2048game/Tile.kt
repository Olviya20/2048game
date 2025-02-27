package com.example.a2048game

class Tile(var value: Int = 0) {

    fun merge(other: Tile) {
        if (this.value == other.value) {
            this.value *= 2
            other.value = 0
        }
    }

    fun reset() {
        this.value = 0
    }
}
