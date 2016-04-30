package com.droidcba.countonme.items

import com.droidcba.countonme.Count
import com.droidcba.countonme.Group
import com.droidcba.countonme.Item
import com.droidcba.countonme.db.DbGroup
import com.droidcba.countonme.db.DbItem
import com.droidcba.countonme.db.Repository
import rx.Observable


/**
 *
 * @author juancho.
 */
class ItemsManager(private val db: Repository) {

    fun getGroups(): Observable<List<Group>> {
        // TODO: can be improved by getting all items and count in memory instead of doing a db query for each element.
        return Observable.create { subscriber ->
            // groups
            val groups = db.getGroups()?.map { group ->
                // items
                val items = db.getItemsByGroupId(group.id)?.map { item ->
                    // counts
                    val counts = db.getCountsByItemId(item.id)?.map {
                        Count(it.id, it.counts, it.year, it.month, it.day)
                    } ?: listOf()
                    Item(item.groupId, counts, item.desc)
                } ?: listOf()

                Group(group.id, items, group.desc)
            } ?: listOf()
            subscriber.onNext(groups)
            subscriber.onCompleted()
        }
    }

    fun createGroup(desc: String): Observable<Group> {
        return insertDb({
            db.insertGroup(DbGroup(desc = desc))
        }, {
            Group(it.toInt(), listOf(), desc)
        })
    }

    fun createItem(groupId: Int, desc: String): Observable<Item> {
        return insertDb({
            db.insertItem(DbItem(desc = desc, groupId = groupId))
        }, {
            Item(it.toInt(), listOf(), desc)
        })
    }

    private fun <T> insertDb(insertFunc: () -> Long, createFunc: (Long) -> T): Observable<T> {
        return Observable.create<T> {
            val id = insertFunc()
            if (id > 0) {
                it.onNext(createFunc(id))
                it.onCompleted()
            } else {
                it.onError(Throwable("There was an error to create the Group."))
            }
        }
    }
}