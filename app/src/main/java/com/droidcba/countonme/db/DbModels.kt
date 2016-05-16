package com.droidcba.countonme.db

/**
 *
 * @author juancho.
 */
data class DbGroup(val id: Int = 0,
                   val desc: String)

data class DbItem(val id: Int = 0,
                  val groupId: Int,
                  val desc: String)

data class DbCount(val id: Int = 0,
                   val itemId: Int,
                   val counts: Int,
                   val year: Int,
                   val month: Int,
                   val day: Int)