package com.project.uas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.uas.databinding.FragmentMyRegistrationsBinding

class MyRegistrationsFragment : Fragment() {

    private lateinit var binding: FragmentMyRegistrationsBinding
    private lateinit var registrationAdapter: RegistrationAdapter
    private var displayedRegistrations: ArrayList<EventRegistration> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyRegistrationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recViewTickets.layoutManager = LinearLayoutManager(context)
        binding.recViewTickets.setHasFixedSize(true)

        registrationAdapter = RegistrationAdapter(displayedRegistrations) {
            refreshTicketList()
        }
        binding.recViewTickets.adapter = registrationAdapter

        binding.btnGoRegister.setOnClickListener {
            val mainAct = activity as? MainActivity
            mainAct?.switchToTab(1)
        }

        refreshTicketList()
    }

    override fun onResume() {
        super.onResume()
        refreshTicketList()
    }

    fun refreshTicketList() {
        if (!::binding.isInitialized) return
        displayedRegistrations.clear()
        displayedRegistrations.addAll(EventBank.registrations)
        registrationAdapter.notifyDataSetChanged()

        binding.txtTotalTickets.text = "Registered E-Tickets (${displayedRegistrations.size})"
        if (displayedRegistrations.isEmpty()) {
            binding.txtEmptyTickets.visibility = View.VISIBLE
            binding.recViewTickets.visibility = View.GONE
        } else {
            binding.txtEmptyTickets.visibility = View.GONE
            binding.recViewTickets.visibility = View.VISIBLE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyRegistrationsFragment()
    }
}
