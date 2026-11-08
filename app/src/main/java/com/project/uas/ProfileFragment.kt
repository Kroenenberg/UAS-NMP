package com.project.uas

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import com.project.uas.databinding.FragmentProfileBinding
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ctx = requireContext()

        loadStudentProfile()

        // Load profile avatar via Picasso (Week 6 slide 5-7)
        val builder = Picasso.Builder(ctx)
        builder.listener { _, _, exception -> exception.printStackTrace() }
        builder.build()
            .load("https://picsum.photos/seed/ubayastudent/300/300")
            .placeholder(R.drawable.ic_person)
            .error(R.drawable.ic_person)
            .into(binding.imgProfileAvatar)

        // Setup WebView (Week 7 slide 16-20)
        binding.webViewCampus.webViewClient = WebViewClient()
        binding.webViewCampus.settings.javaScriptEnabled = true
        val htmlContent = """
            <html>
            <body style="font-family:sans-serif; padding:12px; background:#F4FBF9; color:#161D1D;">
                <h3 style="color:#006A6A; margin-top:0;">Universitas Surabaya (UBAYA)</h3>
                <p><b>Kampus II Tenggilis:</b> Jl. Raya Kalirungkut, Surabaya 60293</p>
                <p><b>SKKK Requirement:</b> Minimum points across Penalaran, Minat Bakat, and Organisasi.</p>
                <p><i>Course: Native Mobile Programming (1604C052) - Gasal 2026/2027</i></p>
            </body>
            </html>
        """.trimIndent()
        binding.webViewCampus.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)

        // Implicit Intent 1: Dial UBAYA Student Center (ACTION_DIAL)
        binding.btnContactBaa.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:0312981000"))
            startActivity(dialIntent)
        }

        // Implicit Intent 2: Open UBAYA Tenggilis Campus in Google Maps (ACTION_VIEW)
        binding.btnOpenCampusMap.setOnClickListener {
            val geoUri = Uri.parse("geo:-7.3204,112.7681?q=Universitas+Surabaya+Tenggilis")
            val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)
            startActivity(mapIntent)
        }

        // Implicit Intent 3: Send Email to Campus Helpdesk (ACTION_SENDTO)
        binding.btnEmailSupport.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:humas@unit.ubaya.ac.id")
                putExtra(Intent.EXTRA_SUBJECT, "UBAYA Campus Hub Inquiry")
            }
            startActivity(Intent.createChooser(emailIntent, "Send Email via"))
        }

        // Logout with AlertDialog confirmation & SharedPreferences clear
        binding.btnLogout.setOnClickListener {
            val alert = AlertDialog.Builder(ctx)
            alert.setTitle("Sign Out")
            alert.setMessage("Do you want to sign out of your student session?")
            alert.setPositiveButton("Sign Out") { _, _ ->
                val shared = ctx.getSharedPreferences(LoginActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE)
                val editor = shared.edit()
                editor.clear()
                editor.apply()

                val intent = Intent(ctx, LoginActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
            alert.setNegativeButton("Cancel") { d, _ -> d.dismiss() }
            alert.create().show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadStudentProfile()
    }

    private fun loadStudentProfile() {
        if (!::binding.isInitialized) return
        val ctx = context ?: return
        val shared = ctx.getSharedPreferences(LoginActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val studentName = shared.getString(LoginActivity.KEY_STUDENT_NAME, "UBAYA Student")
        val studentNrp = shared.getString(LoginActivity.KEY_STUDENT_NRP, "160424001")
        val studentFaculty = shared.getString(LoginActivity.KEY_STUDENT_FACULTY, "Fakultas Teknik")

        // Also check if featured event was passed via BundleCompat (Week 8 slide 23-29)
        arguments?.let {
            val featuredEvent = BundleCompat.getParcelable(it, ARG_FEATURED_EVENT, CampusEvent::class.java)
            if (featuredEvent != null) {
                // Available if passed via newInstance
            }
        }

        binding.txtProfileName.text = studentName
        binding.txtProfileNrp.text = "NRP: $studentNrp"
        binding.txtProfileFaculty.text = studentFaculty
        binding.txtProfileStats.text = "Active Registered Tickets: ${EventBank.registrations.size}"
    }

    companion object {
        private const val ARG_FEATURED_EVENT = "arg_featured_event"

        @JvmStatic
        fun newInstance(featuredEvent: CampusEvent? = null) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    if (featuredEvent != null) {
                        putParcelable(ARG_FEATURED_EVENT, featuredEvent)
                    }
                }
            }
    }
}
