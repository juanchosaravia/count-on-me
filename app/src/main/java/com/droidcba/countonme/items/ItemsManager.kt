package com.droidcba.countonme.items

import com.droidcba.countonme.Count
import com.droidcba.countonme.Group
import com.droidcba.countonme.Item
import com.droidcba.countonme.commons.getDay
import com.droidcba.countonme.commons.getMonth
import com.droidcba.countonme.commons.getYear
import com.droidcba.countonme.db.DbCount
import com.droidcba.countonme.db.DbGroup
import com.droidcba.countonme.db.DbItem
import com.droidcba.countonme.db.Repository
import rx.Observable
import java.util.*


/**
 *
 * @author juancho.
 */
class ItemsManager(private val db: Repository) {

    fun getGroupsByDate(date: Calendar): Observable<List<Group>> {
        // TODO: can be improved by getting all items and count in memory instead of doing a db query for each element.
        return Observable.create { subscriber ->
            // groups
            val groups = db.getGroups()?.map { group ->
                // items
                val items = db.getItemsByGroupId(group.id)?.map { item ->
                    // counts
                    val counts = db.getCountsByItemIdMonthYear(item.id,
                            date.getMonth(),
                            date.getYear())?.map {
                        Count(it.id, it.counts, it.year, it.month, it.day)
                    } ?: listOf()
                    Item(item.groupId, counts.toMutableList(), item.desc)
                } ?: listOf()

                Group(group.id, items.toMutableList(), group.desc)
            } ?: listOf()
            subscriber.onNext(groups)
            subscriber.onCompleted()
        }
    }

    fun createGroup(desc: String): Observable<Group> {
        return insertDb({
            db.insertGroup(DbGroup(desc = desc))
        }, {
            Group(it.toInt(), mutableListOf(), desc)
        })
    }

    fun createItem(group: Group, desc: String): Observable<Item> {
        return insertDb({
            db.insertItem(DbItem(desc = desc, groupId = group.id))
        }, {
            Item(it.toInt(), mutableListOf(), desc)
        })
    }

    fun incrementItem(item: Item, date: Calendar): Observable<Count> {
        return Observable.create<Count> {
            var counts = 1
            var id = 0
            val year = date.getYear()
            val month = date.getMonth()
            val day = date.getDay()

            // TODO: prevent this search by doing a select in the db.
            val count = item.getCountByDate(year, month, day)
            if (count != null) {
                counts += count.counts // add old counts
                id = db.updateCount(
                        DbCount(count.id,
                                item.id,
                                counts,
                                date.getYear(),
                                date.getMonth(),
                                date.getDay()))
            } else {
                id = db.insertCount(DbCount(itemId = item.id,
                        counts = counts,
                        year = date.getYear(),
                        month = date.getMonth(),
                        day = date.getDay())).toInt()
            }
            if (id > 0) {
                it.onNext(Count(id.toInt(), counts, year, month, day))
                it.onCompleted()
            } else {
                it.onError(Throwable("There was an error to increment the item count."))
            }
        }
    }

    private fun <T> insertDb(insertFunc: () -> Long, createFunc: (Long) -> T): Observable<T> {
        return Observable.create<T> {
            val id = insertFunc()
            if (id > 0) {
                it.onNext(createFunc(id))
                it.onCompleted()
            } else {
                it.onError(Throwable("There was an error to create the required element."))
            }
        }
    }
}