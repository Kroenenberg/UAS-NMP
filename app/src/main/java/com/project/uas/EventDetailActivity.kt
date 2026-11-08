package com.project.uas

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BundleCompat
import com.project.uas.databinding.ActivityEventDetailBinding
import com.squareup.picasso.Picasso

class EventDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventDetailBinding

    companion object {
        const val EXTRA_EVENT = "extra_campus_event"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val event = intent.extras?.let {
            BundleCompat.getParcelable(it, EXTRA_EVENT, CampusEvent::class.java)
        }

        if (event == null) {
            finish()
            return
        }

        binding.txtDetailCategory.text = event.category
        binding.txtDetailSkkk.text = "+${event.skkkPoints} SKKK Points"
        binding.txtDetailTitle.text = event.title
        binding.txtDetailOrganizer.text = "Organized by ${event.organizer}"
        binding.txtDetailDate.text = "Event Date: ${event.date}"
        binding.txtDetailLocation.text = "Venue: ${event.location}"
        binding.txtDetailQuota.text = "Participant Quota: ${event.quota} Seats"
        binding.txtDetailContact.text = "Committee Contact: ${event.contactPhone}"
        binding.txtDetailDescription.text = event.description

        // Load event image using Picasso (Week 6 slide 5-7)
        val builder = Picasso.Builder(this)
        builder.listener { _, _, exception -> exception.printStackTrace() }
        builder.build()
            .load(event.imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(binding.imgDetailBanner)

        // Explicit Intent back to MainActivity Registration tab with selected event ID
        binding.btnRegisterNowDetail.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(MainActivity.EXTRA_SELECT_EVENT_ID, event.id)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        // Implicit Intent 1: Share Event Details (Intent.ACTION_SEND - Week 3 slide 28-31)
        binding.btnShareEventDetail.setOnClickListener {
            val shareBody = "Check out this UBAYA campus event!\n\n" +
                "Title: ${event.title}\n" +
                "Category: ${event.category} (+${event.skkkPoints} SKKK)\n" +
                "Date: ${event.date}\n" +
                "Venue: ${event.location}\n" +
                "Organizer: ${event.organizer}\n" +
                "More Info: ${event.websiteUrl}"

            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareBody)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Share Event via")
            startActivity(shareIntent)
        }

        // Implicit Intent 2: Call Event Committee (Intent.ACTION_DIAL)
        binding.btnCallCommittee.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${event.contactPhone}"))
            startActivity(dialIntent)
        }

        // Implicit Intent 3: Open Venue in Google Maps (Intent.ACTION_VIEW with geo URI)
        binding.btnViewLocationMap.setOnClickListener {
            val encodedLocation = Uri.encode("${event.location}, Universitas Surabaya")
            val mapUri = Uri.parse("geo:-7.3204,112.7681?q=$encodedLocation")
            val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
            startActivity(mapIntent)
        }

        binding.btnBackDetail.setOnClickListener {
            finish()
        }
    }
}
