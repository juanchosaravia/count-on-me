package com.droidcba.countonme.db

import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.rowParser
import org.jetbrains.anko.db.select

/**
 *
 * @author juancho.
 */
class RepositorySqlImpl(val sql: CountOnMeDbHelper) : Repository {

    override fun getGroups(): List<DbGroup>? {
        return sql.use {
            select(DbSchema.TABLE_GROUP_NAME)
                    .exec {
                        parseList(
                                rowParser { id: Int, desc: String ->
                                    DbGroup(id, desc)
                                })
                    }
        }
    }

    override fun selectGroupById(id: Int): DbGroup? {
        throw UnsupportedOperationException()
    }

    override fun insertGroup(group: DbGroup): Long {
        return sql.use {
            insert(DbSchema.TABLE_GROUP_NAME, DbSchema.GroupColumns.DESC to group.desc)
        }
    }

    override fun insertItem(item: DbItem): Long {
        return sql.use {
            insert(DbSchema.TABLE_ITEM_NAME,
                    DbSchema.ItemColumns.GROUP_ID to item.groupId,
                    DbSchema.ItemColumns.DESC to item.desc)
        }
    }

    override fun getItemsByGroupId(groupId: Int): List<DbItem>? {
        return sql.use {
            select(DbSchema.TABLE_ITEM_NAME)
                    .where("${DbSchema.ItemColumns.GROUP_ID} = {id}", "id" to groupId)
                    .exec {
                        parseList(rowParser { id: Int, groupId: Int, desc: String ->
                            DbItem(id, groupId, desc)
                        })
                    }
        }
    }

    override fun getCountsByItemId(itemId: Int): List<DbCount>? {
        return sql.use {
            select(DbSchema.TABLE_COUNTS_NAME)
                    .where("${DbSchema.CountsColumns.ITEM_ID} = {id}", "id" to itemId)
                    .exec {
                        parseList(
                                rowParser { id: Int, counts: Int, year: Int, month: Int, day: Int ->
                                    DbCount(id, counts, year, month, day)
                                })
                    }
        }
    }

}