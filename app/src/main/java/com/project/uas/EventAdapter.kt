package com.project.uas

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.uas.databinding.CardEventBinding
import com.squareup.picasso.Picasso

class EventAdapter(val events: ArrayList<CampusEvent>) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(val binding: CardEventBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = CardEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        val context = holder.itemView.context

        holder.binding.txtEventTitle.text = event.title
        holder.binding.txtEventCategory.text = event.category
        holder.binding.txtEventSkkk.text = "+${event.skkkPoints} SKKK"
        holder.binding.txtEventDate.text = event.date
        holder.binding.txtEventLocation.text = event.location
        holder.binding.txtEventOrganizer.text = "By ${event.organizer}"

        // Load event banner image using Picasso (Week 6 slide 5-7)
        val builder = Picasso.Builder(context)
        builder.listener { picasso, uri, exception ->
            exception.printStackTrace()
        }
        builder.build()
            .load(event.imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(holder.binding.imgEventBanner)

        // Explicit Intent to EventDetailActivity (Week 3 & Week 4)
        holder.binding.btnDetailCard.setOnClickListener {
            val intent = Intent(context, EventDetailActivity::class.java)
            intent.putExtra(EventDetailActivity.EXTRA_EVENT, event)
            context.startActivity(intent)
        }

        holder.binding.cardEvent.setOnClickListener {
            val intent = Intent(context, EventDetailActivity::class.java)
            intent.putExtra(EventDetailActivity.EXTRA_EVENT, event)
            context.startActivity(intent)
        }

        // Implicit Intent to Share Event (Week 3 slide 28-31)
        holder.binding.btnShareCard.setOnClickListener {
            val shareText = "Join ${event.title} (${event.category}) on ${event.date} at ${event.location}! Organized by ${event.organizer}. Earn +${event.skkkPoints} SKKK points!"
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Share Campus Event via")
            context.startActivity(shareIntent)
        }
    }
}
