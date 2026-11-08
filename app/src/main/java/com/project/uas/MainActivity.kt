package com.project.uas

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.project.uas.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val fragments: ArrayList<Fragment> = ArrayList()

    private lateinit var eventListFragment: EventListFragment
    private lateinit var registrationFormFragment: RegistrationFormFragment
    private lateinit var myRegistrationsFragment: MyRegistrationsFragment
    private lateinit var profileFragment: ProfileFragment

    companion object {
        const val EXTRA_SELECT_EVENT_ID = "extra_select_event_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Read student session from Intent or SharedPreferences (Week 3)
        val shared = getSharedPreferences(LoginActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val studentName = intent.getStringExtra(LoginActivity.KEY_STUDENT_NAME)
            ?: shared.getString(LoginActivity.KEY_STUDENT_NAME, "Student")
        val studentNrp = intent.getStringExtra(LoginActivity.KEY_STUDENT_NRP)
            ?: shared.getString(LoginActivity.KEY_STUDENT_NRP, "160424001")

        binding.txtWelcomeStudent.text = "Logged in as: $studentName ($studentNrp)"

        // Show DialogFragment on Info button click (Week 8 slide 17-20)
        binding.btnAboutDialog.setOnClickListener {
            val dialog = InfoDialogFragment()
            dialog.show(supportFragmentManager, "info_dialog")
        }

        // Initialize Fragments for ViewPager2 (Week 8 slide 37-47)
        eventListFragment = EventListFragment.newInstance()
        registrationFormFragment = RegistrationFormFragment.newInstance()
        myRegistrationsFragment = MyRegistrationsFragment.newInstance()
        profileFragment = ProfileFragment.newInstance()

        fragments.add(eventListFragment)
        fragments.add(registrationFormFragment)
        fragments.add(myRegistrationsFragment)
        fragments.add(profileFragment)

        binding.viewPager.adapter = MainViewPagerAdapter(this, fragments)

        // Sync ViewPager2 swipe with BottomNavigationView (Week 8 slide 55)
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.bottomNav.selectedItemId = binding.bottomNav.menu.getItem(position).itemId
            }
        })

        // Sync BottomNavigationView clicks with ViewPager2 (Week 8 slide 56)
        binding.bottomNav.setOnItemSelectedListener { item ->
            binding.viewPager.currentItem = when (item.itemId) {
                R.id.itemEvents -> 0
                R.id.itemFilter -> 1
                R.id.itemTickets -> 2
                R.id.itemProfile -> 3
                else -> 0
            }
            true
        }

        // Check if opened from EventDetailActivity for quick registration
        val preselectedEventId = intent.getIntExtra(EXTRA_SELECT_EVENT_ID, -1)
        if (preselectedEventId != -1) {
            binding.viewPager.currentItem = 1
            binding.viewPager.post {
                registrationFormFragment.selectEventById(preselectedEventId)
            }
        }
    }

    fun filterEventsAndSwitchTab(keyword: String, category: String) {
        binding.viewPager.currentItem = 0
        eventListFragment.applyFilter(keyword, category)
    }

    fun switchToTab(index: Int) {
        binding.viewPager.currentItem = index
        if (index == 2) {
            myRegistrationsFragment.refreshTicketList()
        }
    }
}
