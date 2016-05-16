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

    fun getCountByDate(count: Count) = getCountByDate(count.year, count.month, count.day)

    fun getCountByDate(year: Int, month: Int, day: Int) =
            counts.firstOrNull { it.year == year && it.month == month && it.day == day }

    fun getMonthCount(year: Int, month: Int, day: Int) =
            counts.asSequence()
                    .filter { it.day <= day && it.month == month && it.year == year }
                    .sumBy { it.counts }
                    .toInt()
}

data class Count(val id: Int,
            var counts: Int,
            val year: Int,
            val month: Int,
            val day: Int)