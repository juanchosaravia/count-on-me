package com.droidcba.countonme.items

import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.droidcba.countonme.Group
import com.droidcba.countonme.Item
import com.droidcba.countonme.R
import com.droidcba.countonme.commons.*
import com.droidcba.countonme.db.RepositorySqlImpl
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager
import kotlinx.android.synthetic.main.items_fragment.*
import rx.Subscription
import java.util.*

/**
 *
 * @author juancho.
 */
class ItemsFragment : BaseFragment(), ItemsAdapter.onItemsAdapterClicks {

    companion object {
        val TAG = "ItemsFragment"
    }

    private val SAVED_STATE_EXPANDABLE_ITEM_MANAGER = "RecyclerViewExpandableItemManager"

    private val itemsManager by lazy { ItemsManager(RepositorySqlImpl(context.db)) }
    private lateinit var alertCreateGroup: AlertDialog;

    private val linearLayoutManager by lazy { LinearLayoutManager(context) }
    private lateinit var rvItemManager: RecyclerViewExpandableItemManager
    private val adapter by lazy { ItemsAdapter(rvItemManager, this) }
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

            // TODO: Use selected date
            rxLifecycle {
                itemsManager.getGroupsByDate(Calendar.getInstance())
                        .androidOn()
                        .subscribe({ groups ->
                            adapter.setGroups(groups)
                        }, {
                            toast("Something went wrong...")
                        })
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // save current state to support screen rotation, etc...
        outState.putParcelable(SAVED_STATE_EXPANDABLE_ITEM_MANAGER, rvItemManager.savedState)
    }

    fun plusNewGroupClicked() {
        showDialog("Nombre del Grupo") { selectedText ->
            itemsManager.createGroup(selectedText)
                    .androidOn()
                    .subscribe ({ group ->
                        adapter.addGroup(group)
                    }, { e ->
                        toast("No se pudo crear el Grupo. Error: ${e.message}")
                    })
        }
    }

    override fun onGroupPlus(group: Group, groupPosition: Int) {
        showDialog("Nombre del Item") { selectedText ->
            itemsManager.createItem(group, selectedText)
                    .androidOn()
                    .subscribe({ newItem ->
                        adapter.addItem(groupPosition, newItem)
                    }, { e ->
                        toast("No se pudo crear el Item. Error: ${e.message}")
                    })
        }
    }

    override fun onItemIncrement(item: Item, groupPosition: Int) {
        throw UnsupportedOperationException()
    }

    override fun onItemNew(view: View, groupPosition: Int) {
        throw UnsupportedOperationException()
    }

    fun showDialog(title: String, func: (String) -> Subscription) {
        val etGroupName = LayoutInflater.from(context).inflate(R.layout.group_new_dialog, null) as EditText
        val alert = AlertDialog.Builder(context);
        alert.setTitle(title);
        alert.setView(etGroupName);

        alert.setPositiveButton("Aceptar") { dialog, whichButton -> }

        alert.setNegativeButton("Cancelar") { dialog, whichButton -> dialog.dismiss() }
        alertCreateGroup = alert.show();
        alertCreateGroup.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (TextUtils.isEmpty(etGroupName.text)) {
                etGroupName.error = "invalid value."
            } else {
                rxLifecycle {
                    func(etGroupName.text.toString())
                }
                alertCreateGroup.dismiss()
            }
        }
        dialogs.add(alertCreateGroup)
    }
}