package com.droidcba.countonme.commons

import android.content.Context
import com.droidcba.countonme.db.CountOnMeDbHelper
import rx.Observable
import rx.Subscription

/**
 *
 * @author juancho.
 */
// Access property for Context
val Context.db: CountOnMeDbHelper
    get() = CountOnMeDbHelper.getInstance(applicationContext)

fun <T> Observable<T>.androidOn(): Observable<T> {
    return subscribeOn(rx.schedulers.Schedulers.io())
            .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
}