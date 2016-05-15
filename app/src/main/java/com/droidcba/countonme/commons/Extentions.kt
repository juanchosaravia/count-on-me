package com.droidcba.countonme.commons

import android.content.Context
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.droidcba.countonme.db.CountOnMeDbHelper
import rx.Observable
import java.util.*

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

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

fun Calendar.getMonth(): Int {
    return get(Calendar.MONTH)
}

fun Calendar.getYear(): Int {
    return get(Calendar.YEAR)
}

fun Fragment.toast(msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}