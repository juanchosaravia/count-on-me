package com.droidcba.countonme.db

/**
 *
 * @author juancho.
 */
interface Repository {

    fun selectGroupById(id: Int): DbGroup?
    fun insertGroup(group: DbGroup): Long
    fun getGroups(): List<DbGroup>?

    fun getItemsByGroupId(groupId: Int): List<DbItem>?

    fun getCountsByItemId(itemId: Int): List<DbCount>?
    fun getCountsByItemIdMonthYear(itemId: Int, month: Int, year: Int): List<DbCount>?

    fun insertItem(item: DbItem): Long
    fun updateCount(count: DbCount): Int
    fun insertCount(count: DbCount): Long
}