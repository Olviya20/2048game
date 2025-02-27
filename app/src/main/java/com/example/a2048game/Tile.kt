package com.example.a2048game

class Tile(var value: Int = 0) {
    // Задаем начальное значение для плитки (по умолчанию 0)

    // Метод для слияния двух плиток (к примеру, при слиянии с одинаковым значением)
    fun merge(other: Tile) {
        if (this.value == other.value) {
            this.value *= 2
            other.value = 0
        }
    }

    // Метод для сброса плитки (например, при перемещении)
    fun reset() {
        this.value = 0
    }
}
