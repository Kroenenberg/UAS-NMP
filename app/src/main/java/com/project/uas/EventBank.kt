package com.project.uas

object EventBank {

    // Base URL for local XAMPP MySQL webservice (Week 10 slide 19)
    const val BASE_URL = "http://10.0.2.2/ubaya_events/"
    const val GET_EVENTS_URL = BASE_URL + "get_events.php"
    const val ADD_REGISTRATION_URL = BASE_URL + "add_registration.php"
    const val GET_REGISTRATIONS_URL = BASE_URL + "get_registrations.php"

    // Public REST API fallback URL that returns live JSON over Volley when XAMPP is offline
    const val PUBLIC_EVENTS_API_URL = "https://dummyjson.com/recipes?limit=8"

    // Shared JSON response matching get_events.php structure: {"result":"SUCCESS","data":[...]}
    val DEFAULT_EVENTS_JSON = """
        {
          "result": "SUCCESS",
          "data": [
            {
              "id": 1,
              "title": "UBAYA Tech & AI Summit 2026",
              "category": "Seminar",
              "date": "2026-11-20",
              "location": "Auditorium Perpustakaan Lt. 5, Kampus Tenggilis",
              "organizer": "Fakultas Teknik Informatika UBAYA",
              "contact_phone": "0312981395",
              "image_url": "https://picsum.photos/seed/ubayatech/800/450",
              "description": "National seminar exploring Mobile Development, Artificial Intelligence, and Cloud Computing with industry practitioners and UBAYA alumni. Participants receive e-certificate and SKKK Penalaran points.",
              "skkk_points": 15,
              "quota": 250,
              "website_url": "https://www.ubaya.ac.id"
            },
            {
              "id": 2,
              "title": "Android Kotlin Workshop: Zero to Hero",
              "category": "Workshop",
              "date": "2026-11-25",
              "location": "Lab Pemrograman TF 3.2, Kampus Tenggilis",
              "organizer": "KSM Informatika UBAYA",
              "contact_phone": "0312981100",
              "image_url": "https://picsum.photos/seed/ubayakotlin/800/450",
              "description": "Hands-on intensive workshop building native Android apps with Kotlin, RecyclerView, Material 3, and Volley REST API integration.",
              "skkk_points": 20,
              "quota": 60,
              "website_url": "https://www.ubaya.ac.id"
            },
            {
              "id": 3,
              "title": "Industrial IoT & Robotics Hackathon",
              "category": "Competition",
              "date": "2026-12-02",
              "location": "Gedung Serbaguna Fakultas Teknik, Kampus Tenggilis",
              "organizer": "BEM Fakultas Teknik UBAYA",
              "contact_phone": "0312981150",
              "image_url": "https://picsum.photos/seed/ubayarobotics/800/450",
              "description": "24-hour student innovation competition solving smart campus and manufacturing challenges. Open for teams across all faculties.",
              "skkk_points": 30,
              "quota": 120,
              "website_url": "https://www.ubaya.ac.id"
            },
            {
              "id": 4,
              "title": "Creative Entrepreneurship & Startup Expo",
              "category": "Exhibition",
              "date": "2026-12-10",
              "location": "Plaza Fakultas Bisnis & Ekonomika, Kampus Tenggilis",
              "organizer": "Fakultas Bisnis dan Ekonomika UBAYA",
              "contact_phone": "0312981200",
              "image_url": "https://picsum.photos/seed/ubayastartup/800/450",
              "description": "Annual showcase of student startups, culinary innovations, and digital business prototypes with seed funding pitch sessions.",
              "skkk_points": 10,
              "quota": 300,
              "website_url": "https://www.ubaya.ac.id"
            },
            {
              "id": 5,
              "title": "UI/UX Material Design 3 Masterclass",
              "category": "Workshop",
              "date": "2026-12-14",
              "location": "Studio Industri Kreatif, Kampus Tenggilis",
              "organizer": "Fakultas Industri Kreatif UBAYA",
              "contact_phone": "0312981380",
              "image_url": "https://picsum.photos/seed/ubayadesign/800/450",
              "description": "Learn color schemes, typography hierarchy, accessibility, and dynamic theming using Material Design 3 guidelines.",
              "skkk_points": 15,
              "quota": 80,
              "website_url": "https://www.ubaya.ac.id"
            },
            {
              "id": 6,
              "title": "National Cyber Security Capture The Flag",
              "category": "Competition",
              "date": "2026-12-18",
              "location": "Lab Jaringan Komputer TC 2.1, Kampus Tenggilis",
              "organizer": "KSM Cyber Security UBAYA",
              "contact_phone": "0312981399",
              "image_url": "https://picsum.photos/seed/ubayacyber/800/450",
              "description": "Test your web exploitation, cryptography, and reverse engineering skills in UBAYA's annual CTF tournament.",
              "skkk_points": 25,
              "quota": 100,
              "website_url": "https://www.ubaya.ac.id"
            }
          ]
        }
    """.trimIndent()

    var cachedEvents: ArrayList<CampusEvent> = ArrayList()
    var registrations: ArrayList<EventRegistration> = ArrayList()
}
