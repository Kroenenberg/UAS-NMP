# UBAYA Campus Hub — Final Project (UAS)
**Course:** Native Mobile Programming (1604C052) — Gasal 2026-2027  
**Institution:** Universitas Surabaya (UBAYA)  
**Package Name:** `com.project.uas`

---

## 1. Project Overview
**UBAYA Campus Hub** is a native Android application built with **Kotlin** and **XML ViewBinding** that allows UBAYA students to browse upcoming campus events (Seminars, Workshops, Competitions, Exhibitions), filter events by keyword and category, register for SKKK-awarding activities, manage their registered e-tickets, and contact event committees or locate campus venues.

Every component in this project strictly follows the methods, libraries, and architectural patterns taught in **Weeks 1–10** of the UBAYA Native Mobile Programming course.

---

## 2. Syllabus Alignment & Features Mapping (100% Rubric Coverage)

### A. Web Service & JSON Integration (30% Weight — Week 10)
- **Volley (`com.android.volley:volley:1.2.1`)**:
  - Uses `Volley.newRequestQueue(context)` and `StringRequest(Request.Method.POST, url, Response.Listener, Response.ErrorListener)` in [`EventListFragment.kt`](app/src/main/java/com/project/uas/EventListFragment.kt) and [`RegistrationFormFragment.kt`](app/src/main/java/com/project/uas/RegistrationFormFragment.kt).
  - Configured `<uses-permission android:name="android.permission.INTERNET" />` and `android:usesCleartextTraffic="true"` in [`AndroidManifest.xml`](app/src/main/AndroidManifest.xml).
- **JSON & GSON (`com.google.code.gson:gson:2.13.2`)**:
  - Parses JSON responses (`{"result":"SUCCESS","data":[...]}`) using `JSONObject`, `getJSONArray("data")`, `TypeToken<List<CampusEvent>>(){}.type`, and `Gson().fromJson(...)`.
  - Uses `@SerializedName` annotations in [`CampusEvent.kt`](app/src/main/java/com/project/uas/CampusEvent.kt) and [`EventRegistration.kt`](app/src/main/java/com/project/uas/EventRegistration.kt).
- **PHP + MySQLi Backend (`webservice/`)**:
  - [`ubaya_events.sql`](webservice/ubaya_events.sql): Database schema and seed data for `events` and `registrations` tables.
  - [`get_events.php`](webservice/get_events.php): Prepared statement query returning JSON `{ "result": "SUCCESS", "data": [...] }`.
  - [`add_registration.php`](webservice/add_registration.php): Prepared statement `INSERT` handling POST parameters from Volley `getParams()`.
  - [`get_registrations.php`](webservice/get_registrations.php): Prepared statement `SELECT` returning registered tickets.
  - Includes automatic fallback to `EventBank.DEFAULT_EVENTS_JSON` (identical JSON structure) if local XAMPP (`http://10.0.2.2/ubaya_events/`) is not running during demo.

### B. RecyclerView & Basic Form UI Components (25% Weight — Weeks 2, 4, 6, 7)
- **RecyclerView & CardView**:
  - [`EventAdapter.kt`](app/src/main/java/com/project/uas/EventAdapter.kt) with [`card_event.xml`](app/src/main/res/layout/card_event.xml) displays campus events using `LinearLayoutManager`.
  - [`RegistrationAdapter.kt`](app/src/main/java/com/project/uas/RegistrationAdapter.kt) with [`card_registration.xml`](app/src/main/res/layout/card_registration.xml) displays student e-tickets.
- **Picasso (`com.squareup.picasso:picasso:2.8` — Week 6)**:
  - Uses `Picasso.Builder(context)` with error listener and `.load(url).into(imageView)` in `EventAdapter`, `EventDetailActivity`, and `ProfileFragment`.
- **Form & Filter Page (`RegistrationFormFragment.kt` — Weeks 2 & 7)**:
  - **`Spinner`** + `ArrayAdapter` (`simple_spinner_item` & `simple_spinner_dropdown_item`) for category filtering, event selection, and faculty selection.
  - **`RadioGroup` & `RadioButton`** for selecting Attendance Mode (`Onsite (Campus)` vs `Online (Zoom)`).
  - **`CheckBox`** for selecting additional event facilities (`Printed SKKK E-Certificate`, `Campus Snack Box & Lunch`, `Official Event Lanyard & Kit`) and login session persistence.
  - **`EditText` / `TextInputLayout`** with `OutlinedBox`, `endIconMode="clear_text"`, `password_toggle`, and character counters.
  - **`DatePickerDialog` & `Calendar` & `SimpleDateFormat`** (Week 7) on a non-focusable `EditText` storing the database date in `.tag`.
  - **`AlertDialog.Builder`** (Week 7) for form validation, registration confirmation, ticket cancellation, and sign-out confirmation.
  - **`WebView`** (Week 7) in `ProfileFragment` with `WebViewClient` and `javaScriptEnabled = true`.

### C. Multi-Activity, Fragments & Implicit Intents (20% Weight — Weeks 3 & 8)
- **3 Activities**:
  1. [`LoginActivity.kt`](app/src/main/java/com/project/uas/LoginActivity.kt): Student authentication, faculty `Spinner`, and session persistence using **`SharedPreferences`**.
  2. [`MainActivity.kt`](app/src/main/java/com/project/uas/MainActivity.kt): Hosts **`ViewPager2`** + **`FragmentStateAdapter`** ([`MainViewPagerAdapter.kt`](app/src/main/java/com/project/uas/MainViewPagerAdapter.kt)) synchronized with **`BottomNavigationView`** (`registerOnPageChangeCallback` & `setOnItemSelectedListener`).
  3. [`EventDetailActivity.kt`](app/src/main/java/com/project/uas/EventDetailActivity.kt): Receives `@Parcelize` `CampusEvent` via explicit `Intent` and `BundleCompat.getParcelable`.
- **5 Fragments**:
  - [`EventListFragment.kt`](app/src/main/java/com/project/uas/EventListFragment.kt)
  - [`RegistrationFormFragment.kt`](app/src/main/java/com/project/uas/RegistrationFormFragment.kt)
  - [`MyRegistrationsFragment.kt`](app/src/main/java/com/project/uas/MyRegistrationsFragment.kt)
  - [`ProfileFragment.kt`](app/src/main/java/com/project/uas/ProfileFragment.kt)
  - [`InfoDialogFragment.kt`](app/src/main/java/com/project/uas/InfoDialogFragment.kt) (`DialogFragment`)
- **Implicit Intents (Week 3)**:
  1. **Share Event / E-Ticket (`Intent.ACTION_SEND` + `Intent.createChooser`)** in `EventAdapter`, `EventDetailActivity`, and `RegistrationAdapter`.
  2. **Call Committee / UBAYA Student Center (`Intent.ACTION_DIAL` with `tel:` URI)** in `EventDetailActivity` and `ProfileFragment`.
  3. **Open Venue in Maps (`Intent.ACTION_VIEW` with `geo:` URI)** in `EventDetailActivity` and `ProfileFragment`.
  4. **Send Email to Helpdesk (`Intent.ACTION_SENDTO` with `mailto:` URI)** in `ProfileFragment`.

### D. Material Design 3 (15% Weight — Week 9)
- Custom Light and Dark Material 3 color palettes in [`values/colors.xml`](app/src/main/res/values/colors.xml), [`values/themes.xml`](app/src/main/res/values/themes.xml) (`Theme.Material3.Light.NoActionBar`), and [`values-night/themes.xml`](app/src/main/res/values-night/themes.xml) (`Theme.Material3.Dark.NoActionBar`).
- Uses Material 3 typography styles, `OutlinedButton`, `TextButton`, `CardView`, `BottomNavigationView`, and `TextInputLayout`.

---

## 3. Setup Instructions (Web Service & Android Studio)
1. **Optional Local XAMPP Setup**:
   - Copy the contents of the [`webservice/`](webservice) folder into `C:\xampp\htdocs\ubaya_events\`.
   - Start **Apache** and **MySQL** in XAMPP Control Panel.
   - Open `http://localhost/phpmyadmin`, create/import [`webservice/ubaya_events.sql`](webservice/ubaya_events.sql).
   - *(Note: If XAMPP is not running, the app automatically falls back to the built-in JSON dataset in `EventBank.kt` so all features can be tested immediately).*
2. **Run in Android Studio**:
   - Open `c:\Users\Saint Thomas\AndroidStudioProjects\UAS` in Android Studio.
   - Let Gradle sync and click **Run ('app')** on an Android Emulator or physical device.

---

## 4. Declaration of AI Usage (Mandatory Course Policy)
In accordance with the academic integrity guidelines in *Project Native Mobile Programming - 1604C052 (Gasal 2026-2027)*:
- **AI Tool Used:** Google Antigravity (AI Coding Assistant).
- **Scope of Usage:** Used to assist in structuring the Android Studio project files, generating sample UBAYA campus event JSON/SQL seed data, creating XML layouts adhering to Material Design 3 (`themes.xml`, `colors.xml`), and implementing Kotlin classes strictly following the UBAYA NMP Weeks 1–10 lecture materials (ViewBinding, Explicit/Implicit Intents, SharedPreferences, RecyclerView + CardView, Picasso, DatePickerDialog, AlertDialog, ViewPager2 + BottomNavigationView, Volley, and GSON).
