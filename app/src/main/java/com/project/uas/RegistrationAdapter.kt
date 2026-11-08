package com.project.uas

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.project.uas.databinding.CardRegistrationBinding

class RegistrationAdapter(
    val registrations: ArrayList<EventRegistration>,
    val onListChanged: () -> Unit
) : RecyclerView.Adapter<RegistrationAdapter.RegistrationViewHolder>() {

    class RegistrationViewHolder(val binding: CardRegistrationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegistrationViewHolder {
        val binding = CardRegistrationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RegistrationViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return registrations.size
    }

    override fun onBindViewHolder(holder: RegistrationViewHolder, position: Int) {
        val item = registrations[position]
        val context = holder.itemView.context

        holder.binding.txtTicketEventTitle.text = item.eventTitle
        holder.binding.txtTicketStudent.text = "${item.studentName} (${item.nrp}) - ${item.faculty}"
        holder.binding.txtTicketDetails.text = "Mode: ${item.attendanceMode} | Date: ${item.preferredDate}"
        holder.binding.txtTicketExtras.text = "Facilities: ${item.extras} | Notes: ${item.notes}"

        // Implicit Intent to Share E-Ticket (Week 3 slide 28-31)
        holder.binding.btnShareTicket.setOnClickListener {
            val ticketSummary = "UBAYA E-Ticket Confirmation\nEvent: ${item.eventTitle}\nParticipant: ${item.studentName} (${item.nrp})\nAttendance: ${item.attendanceMode} on ${item.preferredDate}\nFacilities: ${item.extras}"
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, ticketSummary)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Share E-Ticket")
            context.startActivity(shareIntent)
        }

        // AlertDialog confirmation to cancel registration (Week 7 slide 11-15)
        holder.binding.btnCancelTicket.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Cancel Registration")
            builder.setMessage("Are you sure you want to cancel your ticket for ${item.eventTitle}?")
            builder.setPositiveButton("Yes, Cancel") { dialog, which ->
                val currentPos = holder.adapterPosition
                if (currentPos != RecyclerView.NO_POSITION) {
                    val removed = registrations.removeAt(currentPos)
                    EventBank.registrations.remove(removed)
                    notifyDataSetChanged()
                    onListChanged()
                    Toast.makeText(context, "Ticket cancelled", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton("Keep Ticket") { dialog, which ->
                dialog.dismiss()
            }
            builder.create().show()
        }
    }
}
