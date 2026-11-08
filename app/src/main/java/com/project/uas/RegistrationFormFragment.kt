package com.project.uas

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.project.uas.databinding.FragmentRegistrationFormBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RegistrationFormFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationFormBinding
    private val categories = arrayOf("All Categories", "Seminar", "Workshop", "Competition", "Exhibition")
    private lateinit var eventSpinnerAdapter: ArrayAdapter<CampusEvent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ctx = requireContext()

        // 1. Setup Category Spinner for Filtering (Week 2 slide 33-38)
        val catAdapter = ArrayAdapter(ctx, android.R.layout.simple_spinner_item, categories)
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategoryFilter.adapter = catAdapter

        // 2. Setup Event Spinner for Registration (Week 2 slide 39-40)
        if (EventBank.cachedEvents.isEmpty()) {
            // Ensure cached events are populated
            try {
                val obj = org.json.JSONObject(EventBank.DEFAULT_EVENTS_JSON)
                val data = obj.getJSONArray("data")
                val sType = object : com.google.gson.reflect.TypeToken<List<CampusEvent>>() {}.type
                val parsed = com.google.gson.Gson().fromJson(data.toString(), sType) as ArrayList<CampusEvent>
                EventBank.cachedEvents.addAll(parsed)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        eventSpinnerAdapter = ArrayAdapter(ctx, android.R.layout.simple_spinner_item, EventBank.cachedEvents)
        eventSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSelectEvent.adapter = eventSpinnerAdapter

        // 3. Pre-fill Student Name and NRP from SharedPreferences (Week 3 slide 12-17)
        val shared = ctx.getSharedPreferences(LoginActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val savedName = shared.getString(LoginActivity.KEY_STUDENT_NAME, "") ?: ""
        val savedNrp = shared.getString(LoginActivity.KEY_STUDENT_NRP, "") ?: ""
        val savedFaculty = shared.getString(LoginActivity.KEY_STUDENT_FACULTY, "Fakultas Teknik") ?: "Fakultas Teknik"

        binding.txtRegName.setText(savedName)
        binding.txtRegNrp.setText(savedNrp)

        // 4. DatePickerDialog setup (Week 7 slide 3-10)
        val today = Calendar.getInstance()
        val displayFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val dbFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        binding.txtPreferredDate.setText(displayFormat.format(today.time))
        binding.txtPreferredDate.tag = dbFormat.format(today.time)

        binding.txtPreferredDate.setOnClickListener {
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                ctx,
                DatePickerDialog.OnDateSetListener { viewPicker, selectedYear, selectedMonth, selectedDay ->
                    val pickedCal = Calendar.getInstance()
                    pickedCal.set(selectedYear, selectedMonth, selectedDay)
                    binding.txtPreferredDate.setText(displayFormat.format(pickedCal.time))
                    binding.txtPreferredDate.tag = dbFormat.format(pickedCal.time)
                },
                year,
                month,
                day
            ).show()
        }

        // 5. Filter Buttons
        binding.btnApplyFilter.setOnClickListener {
            val keyword = binding.txtSearchKeyword.text.toString()
            val selectedCategory = binding.spinnerCategoryFilter.selectedItem.toString()
            val mainAct = activity as? MainActivity
            mainAct?.filterEventsAndSwitchTab(keyword, selectedCategory)
        }

        binding.btnResetFilter.setOnClickListener {
            binding.txtSearchKeyword.setText("")
            binding.spinnerCategoryFilter.setSelection(0)
            val mainAct = activity as? MainActivity
            mainAct?.filterEventsAndSwitchTab("", "All Categories")
        }

        // 6. Registration Submit Button (Spinner + RadioButton + CheckBox + EditText + AlertDialog + Volley POST)
        binding.btnSubmitRegistration.setOnClickListener {
            val participantName = binding.txtRegName.text.toString().trim()
            val participantNrp = binding.txtRegNrp.text.toString().trim()

            if (participantName.isEmpty() || participantNrp.isEmpty()) {
                val alert = AlertDialog.Builder(ctx)
                alert.setTitle("Validation Error")
                alert.setMessage("Please fill in both Participant Full Name and Student NRP.")
                alert.setPositiveButton("OK") { d, _ -> d.dismiss() }
                alert.create().show()
                return@setOnClickListener
            }

            if (EventBank.cachedEvents.isEmpty()) {
                Toast.makeText(ctx, "No events available to register.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedEvent = binding.spinnerSelectEvent.selectedItem as CampusEvent

            // Read RadioButton selection
            val attendanceMode = if (binding.rbOnsite.isChecked) {
                "Onsite (Campus)"
            } else {
                "Online (Zoom)"
            }

            // Read CheckBoxes selection
            val facilitiesList = ArrayList<String>()
            if (binding.cbCertificate.isChecked) facilitiesList.add("E-Certificate")
            if (binding.cbSnackBox.isChecked) facilitiesList.add("Snack Box")
            if (binding.cbMerchandise.isChecked) facilitiesList.add("Event Kit")
            val facilitiesStr = if (facilitiesList.isEmpty()) "None" else facilitiesList.joinToString(", ")

            val dateValue = binding.txtPreferredDate.tag?.toString() ?: binding.txtPreferredDate.text.toString()
            val notesValue = binding.txtNotes.text.toString().trim().ifEmpty { "-" }

            val newReg = EventRegistration(
                eventId = selectedEvent.id,
                eventTitle = selectedEvent.title,
                studentName = participantName,
                nrp = participantNrp,
                faculty = savedFaculty,
                attendanceMode = attendanceMode,
                extras = facilitiesStr,
                preferredDate = dateValue,
                notes = notesValue
            )

            // Confirm via AlertDialog (Week 7 slide 11-15)
            val confirmBuilder = AlertDialog.Builder(ctx)
            confirmBuilder.setTitle("Confirm Event Registration")
            confirmBuilder.setMessage(
                "Event: ${newReg.eventTitle}\n" +
                "Participant: ${newReg.studentName} (${newReg.nrp})\n" +
                "Mode: ${newReg.attendanceMode}\n" +
                "Facilities: ${newReg.extras}\n" +
                "Date: ${newReg.preferredDate}\n\n" +
                "Proceed with registration?"
            )
            confirmBuilder.setPositiveButton("Confirm") { _, _ ->
                submitRegistrationToWebService(newReg)
            }
            confirmBuilder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            confirmBuilder.create().show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::eventSpinnerAdapter.isInitialized) {
            eventSpinnerAdapter.notifyDataSetChanged()
        }
    }

    fun selectEventById(eventId: Int) {
        if (!::binding.isInitialized) return
        val index = EventBank.cachedEvents.indexOfFirst { it.id == eventId }
        if (index >= 0) {
            binding.spinnerSelectEvent.setSelection(index)
        }
    }

    private fun submitRegistrationToWebService(reg: EventRegistration) {
        val ctx = context ?: return
        EventBank.registrations.add(0, reg)

        val q = Volley.newRequestQueue(ctx)
        val stringRequest = object : StringRequest(
            Request.Method.POST,
            EventBank.ADD_REGISTRATION_URL,
            Response.Listener<String> { response ->
                Log.d("apiresult", response)
                Toast.makeText(ctx, "Registered & Saved to Server!", Toast.LENGTH_SHORT).show()
                navigateToTicketsTab()
            },
            Response.ErrorListener { error ->
                Log.e("apiresult", error.message.toString())
                Toast.makeText(ctx, "E-Ticket Generated!", Toast.LENGTH_SHORT).show()
                navigateToTicketsTab()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["event_id"] = reg.eventId.toString()
                params["event_title"] = reg.eventTitle
                params["student_name"] = reg.studentName
                params["nrp"] = reg.nrp
                params["faculty"] = reg.faculty
                params["attendance_mode"] = reg.attendanceMode
                params["extras"] = reg.extras
                params["preferred_date"] = reg.preferredDate
                params["notes"] = reg.notes
                return params
            }
        }
        q.add(stringRequest)
    }

    private fun navigateToTicketsTab() {
        binding.txtNotes.setText("")
        val mainAct = activity as? MainActivity
        mainAct?.switchToTab(2)
    }

    companion object {
        @JvmStatic
        fun newInstance() = RegistrationFormFragment()
    }
}
