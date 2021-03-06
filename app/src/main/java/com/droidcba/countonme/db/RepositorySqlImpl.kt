package com.droidcba.countonme.db

import org.jetbrains.anko.db.*

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

    override fun insertCount(count: DbCount): Long {
        return sql.use {
            insert(DbSchema.TABLE_COUNTS_NAME,
                    DbSchema.CountsColumns.ITEM_ID to count.itemId,
                    DbSchema.CountsColumns.COUNT to count.counts,
                    DbSchema.CountsColumns.YEAR to count.year,
                    DbSchema.CountsColumns.MONTH to count.month,
                    DbSchema.CountsColumns.DAY to count.day
            )
        }
    }

    override fun updateCount(count: DbCount): Int {
        return sql.use {
            update(DbSchema.TABLE_COUNTS_NAME,
                    DbSchema.CountsColumns.COUNT to count.counts)
                    .where("${DbSchema.WHERE_ID}", "id" to count.id)
                    .exec()
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

    override fun getCountsByItemIdMonthYear(itemId: Int, month: Int, year: Int): List<DbCount>? {
        return sql.use {
            select(DbSchema.TABLE_COUNTS_NAME)
                    .where("${DbSchema.CountsColumns.ITEM_ID} = {id}" +
                            " AND ${DbSchema.CountsColumns.MONTH} = {month}" +
                            " AND ${DbSchema.CountsColumns.YEAR} = {year}",
                            "id" to itemId, "month" to month, "year" to year)
                    .exec {
                        parseList (
                                rowParser { id: Int, itemId: Int, counts: Int, year: Int, month: Int, day: Int ->
                                    DbCount(id, itemId, counts, year, month, day)
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
                                    DbCount(id, itemId, counts, year, month, day)
                                })
                    }
        }
    }

}