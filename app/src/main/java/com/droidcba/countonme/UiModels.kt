package com.droidcba.countonme

/**
 *
 * @author juancho.
 */
class Group(val id: Int,
            val items: List<Item>,
            val desc: String)

class Item(val id: Int,
           val counts: List<Count>,
           val desc: String)

class Count(val id: Int,
            val counts: Int,
            val year: Int,
            val month: Int,
            val day: Int)