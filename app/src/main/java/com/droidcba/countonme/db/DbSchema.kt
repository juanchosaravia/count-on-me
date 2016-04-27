package com.droidcba.countonme.db

import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

/**
 *
 * @author juancho.
 */
object DbSchema {

    val TABLE_GROUP_NAME = "Groups"
    val TABLE_ITEM_NAME = "Items"
    val TABLE_COUNTS_NAME = "Counts"

    object GroupColumns {
        val DESC = "desc"
    }

    object ItemColumns {
        val GROUP_ID = "groupId"
        val DESC = "desc"
    }

    object CountsColumns {
        val ITEM_ID = "itemId"
        val COUNT = "count"
        val YEAR = "year"
        val MONTH = "month"
        val DAY = "day"
    }

    // ********* Group *********

    fun SQLiteDatabase.insertGroup(group: DbGroup): DbGroup {
        val groupId = insert(DbSchema.TABLE_GROUP_NAME,
                DbSchema.GroupColumns.DESC to group.desc)
        return group.copy(id = groupId.toInt())
    }

    fun SQLiteDatabase.selectGroupById(id: Int): DbGroup? {
        return select(DbSchema.TABLE_GROUP_NAME)
                .where("_id = {id}", "id" to id)
                .exec {
                    parseOpt(rowParser { id: Int, desc: String ->
                        DbGroup(id, desc)
                    })
                }
    }

    // ********* ITEM *********

    fun SQLiteDatabase.insertItem(newItem: DbItem): DbItem {
        val itemId = insert(DbSchema.TABLE_ITEM_NAME,
                DbSchema.ItemColumns.GROUP_ID to newItem.groupId,
                DbSchema.ItemColumns.DESC to newItem.desc)
        // TODO: check for (itemId == -1)
        return newItem.copy(id = itemId.toInt())
    }

    fun SQLiteDatabase.getItemsByGroupId(groupId: Int): List<DbItem>? {
        return select(DbSchema.TABLE_ITEM_NAME)
                .where("groupId = {id}", "id" to groupId)
                .exec {
                    parseList(rowParser { id: Int, groupId: Int, desc: String ->
                        DbItem(id, groupId, desc)
                    })
                }
    }

    fun SQLiteDatabase.getItemById(id: Int): DbItem? {
        return select(DbSchema.TABLE_ITEM_NAME)
                .where("_id = {id}", "id" to id)
                .exec {
                    parseOpt(rowParser { id: Int, groupId: Int, desc: String ->
                        DbItem(id, groupId, desc)
                    })
                }
    }
}