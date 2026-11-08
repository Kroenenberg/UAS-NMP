package com.project.uas

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.project.uas.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    companion object {
        const val SHARED_PREF_NAME = "com.project.uas.sharedpref"
        const val KEY_STUDENT_NAME = "student_name"
        const val KEY_STUDENT_NRP = "student_nrp"
        const val KEY_STUDENT_FACULTY = "student_faculty"
    }

    private val faculties = arrayOf(
        "Fakultas Teknik (Informatics / Engineering)",
        "Fakultas Bisnis dan Ekonomika (FBE)",
        "Fakultas Industri Kreatif (FIK)",
        "Fakultas Farmasi",
        "Fakultas Psikologi",
        "Fakultas Hukum",
        "Fakultas Teknobiologi",
        "Fakultas Kedokteran"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Faculty Spinner (Week 2 slide 33-38)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, faculties)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFacultyLogin.adapter = adapter

        // Check existing session in SharedPreferences (Week 3 slide 12-17)
        val shared = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val savedName = shared.getString(KEY_STUDENT_NAME, null)
        val savedNrp = shared.getString(KEY_STUDENT_NRP, null)
        val savedFaculty = shared.getString(KEY_STUDENT_FACULTY, faculties[0])

        if (!savedName.isNullOrEmpty() && !savedNrp.isNullOrEmpty()) {
            binding.txtStudentName.setText(savedName)
            binding.txtNrp.setText(savedNrp)
            val facIdx = faculties.indexOf(savedFaculty)
            if (facIdx >= 0) {
                binding.spinnerFacultyLogin.setSelection(facIdx)
            }
        }

        // Quick Fill Demo button for easy testing
        binding.btnDemoFill.setOnClickListener {
            binding.txtStudentName.setText("Saint Thomas")
            binding.txtNrp.setText("160424001")
            binding.txtPassword.setText("ubaya2026")
            binding.spinnerFacultyLogin.setSelection(0)
            binding.cbRememberMe.isChecked = true
        }

        // Sign In Button
        binding.btnLogin.setOnClickListener {
            val studentName = binding.txtStudentName.text.toString().trim()
            val studentNrp = binding.txtNrp.text.toString().trim()
            val password = binding.txtPassword.text.toString().trim()
            val selectedFaculty = binding.spinnerFacultyLogin.selectedItem.toString()

            if (studentName.isEmpty() || studentNrp.isEmpty() || password.isEmpty()) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Incomplete Login")
                builder.setMessage("Please fill in your Student Full Name, NRP, and Password (or tap 'Quick Fill Demo Account').")
                builder.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                builder.create().show()
                return@setOnClickListener
            }

            // Save student credentials in SharedPreferences (Week 3 slide 16-17)
            if (binding.cbRememberMe.isChecked) {
                val editor = shared.edit()
                editor.putString(KEY_STUDENT_NAME, studentName)
                editor.putString(KEY_STUDENT_NRP, studentNrp)
                editor.putString(KEY_STUDENT_FACULTY, selectedFaculty)
                editor.apply()
            }

            Toast.makeText(this, "Welcome to UBAYA Campus Hub, $studentName!", Toast.LENGTH_SHORT).show()

            // Explicit Intent to MainActivity (Week 3 slide 19-24)
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(KEY_STUDENT_NAME, studentName)
            intent.putExtra(KEY_STUDENT_NRP, studentNrp)
            intent.putExtra(KEY_STUDENT_FACULTY, selectedFaculty)
            startActivity(intent)
            finish()
        }
    }
}
