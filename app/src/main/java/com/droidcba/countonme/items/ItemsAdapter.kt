package com.droidcba.countonme.items

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.droidcba.countonme.Count
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
        val rvItemManager: RecyclerViewExpandableItemManager
        //val dataProvider: Repository
) : AbstractExpandableItemAdapter<ItemsAdapter.GroupViewHolder, ItemsAdapter.ItemViewHolder>() {

    var year: Int = 0
    var month: Int = 0
    var day: Int = 0

    var data = listOf<Group>()
    val onGroupClick = View.OnClickListener { handleGroupClick(it) }
    val onItemClick = View.OnClickListener { handleItemClick(it) }

    init {
        // ExpandableItemAdapter requires stable ID, and also
        // have to implement the getGroupItemId()/getChildItemId() methods appropriately.
        setHasStableIds(true)
    }

    fun setDate(year: Int, month: Int, day: Int) {
        this.year = year
        this.month = month
        this.day = day
        data = listOf(
                Group(0, listOf(
                        Item(0, listOf(Count(0, 4, 2016, 4, 30)), "Pastilla Tos"),
                        Item(1, listOf(Count(1, 5, 2016, 4, 30)), "Salir a correr")
                ), "Salud"),
                Group(1, listOf(
                        Item(2, listOf(Count(2, 0, 2016, 4, 30)), "Denario"),
                        Item(3, listOf(Count(3, 1, 2016, 4, 30)), "Visita Santuario")
                ), "Relacion con Dios")
        )
        notifyDataSetChanged()
    }

    override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = parent.inflate(R.layout.items_list_group)
        return GroupViewHolder(view, onGroupClick)
    }

    override fun onBindGroupViewHolder(holder: GroupViewHolder, groupPosition: Int, viewType: Int) {
        holder.bind(data[groupPosition])
    }

    override fun onCreateChildViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = parent.inflate(R.layout.items_list_item)
        return ItemViewHolder(view, onItemClick)
    }

    override fun onBindChildViewHolder(holder: ItemViewHolder, groupPosition: Int, childPosition: Int, viewType: Int) {
        holder.bind(data[groupPosition].items[childPosition])
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

    class GroupViewHolder(v: View, val clickListener: View.OnClickListener) : AbstractExpandableItemViewHolder(v) {

        fun bind(group: Group) = with(itemView) {
            groupPlusButton.setOnClickListener {
                // TODO: ??
            }
            tvDescription.text = group.desc
            setOnClickListener(clickListener)
        }
    }

    inner class ItemViewHolder(v: View, val clickListener: View.OnClickListener) : AbstractExpandableItemViewHolder(v) {
        fun bind(item: Item) = with(itemView) {
            tvTitle.text = item.desc
            tvCounter.text = item.getCountByDate(year, month, day)?.counts.toString()
            setOnClickListener(clickListener)
        }
    }

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