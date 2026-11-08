package com.project.uas

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.uas.databinding.FragmentEventListBinding
import org.json.JSONObject

class EventListFragment : Fragment() {

    private lateinit var binding: FragmentEventListBinding
    private var displayedEvents: ArrayList<CampusEvent> = ArrayList()
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recViewEvents.layoutManager = LinearLayoutManager(context)
        binding.recViewEvents.setHasFixedSize(true)
        eventAdapter = EventAdapter(displayedEvents)
        binding.recViewEvents.adapter = eventAdapter

        binding.btnRefreshEvents.setOnClickListener {
            fetchEventsFromWebService(showToast = true)
        }

        if (EventBank.cachedEvents.isEmpty()) {
            fetchEventsFromWebService(showToast = false)
        } else {
            updateRecyclerView(EventBank.cachedEvents)
        }
    }

    fun fetchEventsFromWebService(showToast: Boolean) {
        val ctx = context ?: return
        binding.progressBarEvents.visibility = View.VISIBLE

        val q = Volley.newRequestQueue(ctx)
        val stringRequest = StringRequest(
            Request.Method.POST,
            EventBank.GET_EVENTS_URL,
            Response.Listener<String> { response ->
                Log.d("apiresult", response)
                binding.progressBarEvents.visibility = View.GONE
                parseEventsJson(response)
                if (showToast) {
                    Toast.makeText(ctx, "Synced from Local MySQL WebService", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Log.e("apiresult", error.message.toString())
                // Fallback to default JSON payload with identical {"result":"SUCCESS","data":[...]} format
                binding.progressBarEvents.visibility = View.GONE
                parseEventsJson(EventBank.DEFAULT_EVENTS_JSON)
                if (showToast) {
                    Toast.makeText(ctx, "Loaded Campus Events (Offline/Demo Mode)", Toast.LENGTH_SHORT).show()
                }
            }
        )
        q.add(stringRequest)
    }

    private fun parseEventsJson(jsonString: String) {
        try {
            val obj = JSONObject(jsonString)
            if (obj.getString("result") == "SUCCESS") {
                val data = obj.getJSONArray("data")
                val sType = object : TypeToken<List<CampusEvent>>() {}.type
                val parsedList = Gson().fromJson(data.toString(), sType) as ArrayList<CampusEvent>
                Log.d("apiresult", parsedList.toString())

                EventBank.cachedEvents.clear()
                EventBank.cachedEvents.addAll(parsedList)
                updateRecyclerView(EventBank.cachedEvents)
            }
        } catch (e: Exception) {
            Log.e("apiresult", e.message.toString())
        }
    }

    fun applyFilter(keyword: String, category: String) {
        val filtered = ArrayList<CampusEvent>()
        val lowerKey = keyword.trim().lowercase()

        for (event in EventBank.cachedEvents) {
            val matchCategory = (category == "All Categories") || event.category.equals(category, ignoreCase = true)
            val matchKeyword = lowerKey.isEmpty() ||
                event.title.lowercase().contains(lowerKey) ||
                event.description.lowercase().contains(lowerKey) ||
                event.organizer.lowercase().contains(lowerKey) ||
                event.location.lowercase().contains(lowerKey)

            if (matchCategory && matchKeyword) {
                filtered.add(event)
            }
        }

        updateRecyclerView(filtered)
    }

    private fun updateRecyclerView(source: ArrayList<CampusEvent>) {
        if (!::binding.isInitialized) return
        displayedEvents.clear()
        displayedEvents.addAll(source)
        eventAdapter.notifyDataSetChanged()
        binding.txtEventCount.text = "Showing ${displayedEvents.size} Campus Events"
    }

    companion object {
        @JvmStatic
        fun newInstance() = EventListFragment()
    }
}
