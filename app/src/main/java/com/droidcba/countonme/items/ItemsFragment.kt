package com.droidcba.countonme.items

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.droidcba.countonme.R
import com.droidcba.countonme.commons.inflate
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager
import kotlinx.android.synthetic.main.items_fragment.*

/**
 *
 * @author juancho.
 */
class ItemsFragment : Fragment() {

    private val SAVED_STATE_EXPANDABLE_ITEM_MANAGER = "RecyclerViewExpandableItemManager"

    private val linearLayoutManager by lazy { LinearLayoutManager(context) }
    private lateinit var rvItemManager: RecyclerViewExpandableItemManager
    private val adapter by lazy { ItemsAdapter(rvItemManager) }
    private val adapterWrapped by lazy { rvItemManager.createWrappedAdapter(adapter) }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.items_fragment)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eimSavedState = savedInstanceState?.getParcelable<Parcelable>(SAVED_STATE_EXPANDABLE_ITEM_MANAGER)
        rvItemManager = RecyclerViewExpandableItemManager(eimSavedState)

        rvItems.apply {
            layoutManager = linearLayoutManager
            itemAnimator = RefactoredDefaultItemAnimator().apply {
                supportsChangeAnimations = false
            }
            setHasFixedSize(false)
            adapter = adapterWrapped
        }

        // TODO: Review this:
        // Expand all group items if no saved state exists.
        // The expandAll() method should be called here (before attaching the RecyclerView instance),
        // because it can reduce overheads of updating item views.
        if (eimSavedState == null) {
            rvItemManager.expandAll()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // save current state to support screen rotation, etc...
        outState.putParcelable(
                SAVED_STATE_EXPANDABLE_ITEM_MANAGER,
                rvItemManager.savedState)
    }
}