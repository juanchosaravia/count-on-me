package com.droidcba.countonme

/**
 *
 * @author juancho.
 */
class Group(val id: Int,
            val items: MutableList<Item>,
            val desc: String)

class Item(val id: Int,
           val counts: MutableList<Count>,
           val desc: String) {

    fun getCountByDate(year: Int, month: Int, day: Int) =
            counts.firstOrNull { it.year == year && it.month == month && it.day == day }
}

class Count(val id: Int,
            val counts: Int,
            val year: Int,
            val month: Int,
            val day: Int)