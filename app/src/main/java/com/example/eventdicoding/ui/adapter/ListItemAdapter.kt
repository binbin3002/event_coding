package com.example.eventdicoding.ui.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventdicoding.R
import com.example.eventdicoding.data.local.entity.EventEntity
import com.example.eventdicoding.ui.detail.DetailActivity
import com.example.eventdicoding.ui.viewmodel.MainViewModel
import com.example.eventdicoding.util.SimpleDateUtil.formatDateTime

class ListItemAdapter(private  val viewModel: MainViewModel): RecyclerView.Adapter<ListItemAdapter.ListEventViewHolder>() {
    private  val events = mutableListOf<EventEntity>()

    fun setEvents(newEvents: List<EventEntity>){
        val diffCallback = EventDiffCallback(events, newEvents)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        events.clear()
        events.addAll(newEvents)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListEventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ListEventViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListEventViewHolder, position: Int) {
        val event : EventEntity =  events[position]
        holder.bind(event)
    }

    override fun getItemCount(): Int = events.size

    inner class  ListEventViewHolder(private  val view: View) : RecyclerView.ViewHolder(view){
        private  val eventName: TextView = view.findViewById(R.id.tv_item_name)
        private  val eventOwner : TextView = view.findViewById(R.id.tv_item_ownerName)
        private  val eventBeginTime : TextView = view.findViewById(R.id.tv_item_beginTime)
        private  val eventQuota : TextView = view.findViewById(R.id.tv_item_quota)
        private  val eventImage : ImageView = view.findViewById(R.id.iv_image_logo)
        private  val btnFavorite : Button = view.findViewById(R.id.btn_favorite)

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(event: EventEntity){
            eventName.text = event.name
            eventOwner.text = event.ownerName
            eventBeginTime.text = formatDateTime(event.beginTime)
            "${event.registrants}/${event.quota}".also { eventQuota.text = it }

            Glide.with(view.context)
                .load(event.imageLogo)
                .into(eventImage)

            val currentNightMode = view.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            val drawable  = ContextCompat.getDrawable(view.context, R.drawable.icon_calender )
            val drawable1  = ContextCompat.getDrawable(view.context, R.drawable.icon_apartment )
            val drawable2  = ContextCompat.getDrawable(view.context, R.drawable.icon_group )

            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES){
                drawable?.setTint(ContextCompat.getColor(view.context, R.color.white))
                drawable1?.setTint(ContextCompat.getColor(view.context, R.color.white))
                drawable2?.setTint(ContextCompat.getColor(view.context, R.color.white))

            }else{
                drawable?.setTint(ContextCompat.getColor(view.context , R.color.black))
                drawable1?.setTint(ContextCompat.getColor(view.context , R.color.black))
                drawable2?.setTint(ContextCompat.getColor(view.context , R.color.black))
            }
            eventBeginTime.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            eventOwner.setCompoundDrawablesWithIntrinsicBounds(drawable1, null, null, null)
            eventQuota.setCompoundDrawablesWithIntrinsicBounds(drawable2, null, null, null)

            view.setOnClickListener{
                val intent = Intent(view.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_EVENT_ID, event.id)
                view.context.startActivity(intent)
            }
            if (event.isFavorited){
                btnFavorite.background = view.context.getDrawable(R.drawable.icon_favorite)
            }else{
                btnFavorite.background = view.context.getDrawable(R.drawable.icon_unfavorite)
            }
            btnFavorite.setOnClickListener{
                event.isFavorited = !event.isFavorited
                btnFavorite.background = if (event.isFavorited){
                    view.context.getDrawable(R.drawable.icon_favorite)

                }else{
                    view.context.getDrawable(R.drawable.icon_unfavorite)
                }
                viewModel.updateFavoriteStatus(event, event.isFavorited)
            }
        }
    }
    private  class EventDiffCallback(
        private  val oldList : List<EventEntity>,
        private val newList: List<EventEntity>
    ): DiffUtil.Callback(){
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return  oldList[oldItemPosition].id == newList[newItemPosition].id
        }
        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return  oldList[oldItemPosition] == newList[newItemPosition]
        }




    }
}