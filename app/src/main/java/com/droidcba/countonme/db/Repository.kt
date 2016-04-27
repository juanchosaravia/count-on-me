package com.droidcba.countonme.db

/**
 *
 * @author juancho.
 */
interface Repository {

    fun selectGroupById(id: Int): DbGroup?
    fun insertGroup(group: DbGroup): Long
    fun getGroups() : List<DbGroup>?

    fun getItemsByGroupId(groupId: Int): List<DbItem>?

    fun getCountsByItemId(itemId: Int): List<DbCount>?
    fun insertItem(item: DbItem): Long
}