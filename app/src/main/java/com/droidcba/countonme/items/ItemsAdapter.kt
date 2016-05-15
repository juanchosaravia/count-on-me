package com.droidcba.countonme.items

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.droidcba.countonme.Group
import com.droidcba.countonme.Item
import com.droidcba.countonme.R
import com.droidcba.countonme.commons.inflate
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils
import kotlinx.android.synthetic.main.items_list_group.view.*
import kotlinx.android.synthetic.main.items_list_item.view.*

/**
 *
 * @author juancho.
 */
class ItemsAdapter(
        val rvItemManager: RecyclerViewExpandableItemManager,
        val onItemsClick: onItemsAdapterClicks
        //val dataProvider: Repository
) : AbstractExpandableItemAdapter<ItemsAdapter.GroupViewHolder, ItemsAdapter.ItemViewHolder>() {

    interface onItemsAdapterClicks {
        fun onGroupPlus(group: Group, groupPosition: Int)
        fun onItemNew(view: View, groupPosition: Int)
        fun onItemIncrement(item: Item, groupPosition: Int)
    }

    var year: Int = 0
    var month: Int = 0
    var day: Int = 0

    var data = mutableListOf<Group>()
    val onGroupClick = View.OnClickListener { handleGroupClick(it) }
    val onItemClick = View.OnClickListener { handleItemClick(it) }

    init {
        // ExpandableItemAdapter requires stable ID, and also
        // have to implement the getGroupItemId()/getChildItemId() methods appropriately.
        setHasStableIds(true)
    }

    override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = parent.inflate(R.layout.items_list_group)
        return GroupViewHolder(view, onGroupClick)
    }

    override fun onBindGroupViewHolder(holder: GroupViewHolder, groupPosition: Int, viewType: Int) {
        holder.bind(data[groupPosition], groupPosition)
    }

    override fun onCreateChildViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = parent.inflate(R.layout.items_list_item)
        return ItemViewHolder(view, onItemClick)
    }

    override fun onBindChildViewHolder(holder: ItemViewHolder, groupPosition: Int, childPosition: Int, viewType: Int) {
        holder.bind(data[groupPosition].items[childPosition], groupPosition, childPosition)
    }

    override fun getGroupCount() = data.size

    override fun getChildCount(groupPosition: Int) = data[groupPosition].items.size

    override fun onCheckCanExpandOrCollapseGroup(holder: GroupViewHolder?, groupPosition: Int, x: Int, y: Int, expand: Boolean): Boolean {
        // NOTE: Handles all click events manually
        return false
    }

    override fun getGroupId(groupPosition: Int) = data[groupPosition].id.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int)
            = data[groupPosition].items[childPosition].id.toLong()

    /**
     *  View Holders
     */

    inner class GroupViewHolder(v: View, val clickListener: View.OnClickListener) : AbstractExpandableItemViewHolder(v) {
        fun bind(group: Group, groupPosition: Int) = with(itemView) {
            groupPlusButton.setOnClickListener {
                onItemsClick.onGroupPlus(group, groupPosition)
            }
            tvDescription.text = group.desc
            setOnClickListener(clickListener)
        }
    }

    inner class ItemViewHolder(v: View, val clickListener: View.OnClickListener) : AbstractExpandableItemViewHolder(v) {
        fun bind(item: Item, groupPosition: Int, itemPosition: Int) = with(itemView) {
            tvTitle.text = item.desc
            val count = item.getCountByDate(year, month, day)
            tvCounter.text = if (count != null) count.counts.toString() else "0"
            setOnClickListener(clickListener)
        }
    }

    /**
     * Handle Click Actions
     */

    fun setGroups(groups: List<Group>) {
        data = groups.toMutableList()
        notifyDataSetChanged()
    }

    fun addGroup(group: Group) {
        data.add(group)
        notifyItemInserted(data.lastIndex)
    }

    fun addItem(groupPosition: Int, item: Item) {
        data[groupPosition].items.add(item)
        notifyDataSetChanged()
    }

    /**
     * Handle Clicks
     */

    fun handleGroupClick(view: View) {
        val vh = RecyclerViewAdapterUtils.getViewHolder(view)
        val flatPosition = vh.adapterPosition

        if (flatPosition == RecyclerView.NO_POSITION) {
            return
        }
        val (groupPosition, itemPosition) = getPositions(flatPosition)

        // toggle expanded/collapsed
        if (isGroupExpanded(groupPosition)) {
            rvItemManager.collapseGroup(groupPosition)
        } else {
            rvItemManager.expandGroup(groupPosition)
        }
    }

    fun handleItemClick(view: View) {
        val vh = RecyclerViewAdapterUtils.getViewHolder(view)
        val flatPosition = vh.adapterPosition

        if (flatPosition == RecyclerView.NO_POSITION) {
            return
        }
        val (groupPos, itemPos) = getPositions(flatPosition)

    }

    fun getPositions(flatPosition: Int): Pair<Int, Int> {
        val expandablePosition = rvItemManager.getExpandablePosition(flatPosition)
        val groupPosition = RecyclerViewExpandableItemManager.getPackedPositionGroup(expandablePosition)
        val childPosition = RecyclerViewExpandableItemManager.getPackedPositionChild(expandablePosition)
        return Pair(groupPosition, childPosition)
    }

    private fun isGroupExpanded(groupPosition: Int): Boolean {
        return rvItemManager.isGroupExpanded(groupPosition)
    }

}