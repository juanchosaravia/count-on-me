package com.droidcba.countonme.commons

import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import rx.Observable
import rx.Subscription
import rx.subscriptions.CompositeSubscription

open class BaseFragment : Fragment() {

    protected var subscriptions = CompositeSubscription()
    protected var dialogs = arrayListOf<AlertDialog>()

    override fun onResume() {
        super.onResume()
        subscriptions = CompositeSubscription()
        dialogs = arrayListOf<AlertDialog>()
    }

    override fun onPause() {
        super.onPause()
        subscriptions.clear()
        dialogs.map { it.dismiss() } // close all dialogs
    }

    fun rxLifecycle(func: () -> Subscription) {
        subscriptions.add(func())
    }
}