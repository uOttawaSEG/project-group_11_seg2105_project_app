Group 11

Team Members:
Mamadou Diallo
Mahdi Hassoun
Atique Aqtab
Kashif Karim
Zachary Yassine
Eren Arikan

Administrator Account (for testing):
Email: admin@uottawa.ca

Password: admin123

Sample Tutor Account:
Email: tutor@uottawa.ca

Password: teach123

Sample Student Account:
Email: student@uottawa.ca

Password: pass123

How to Run:

Download the APK attached in this GitHub release: Project_Group_11_debug.apk
Install it on an Android device or emulator (Android 10+).
Launch the app and log in as Administrator, Tutor, or Student once accounts exist.

Implemented Features:

Student and Tutor registration with InputValidator.
Registration requests saved with PENDING status in SQLite.
Admin approval system for reviewing and approving or rejecting pending requests.
Rejected Requests Activity to re-approve previously rejected users.
Email intent integration (Bonus): admin actions open Gmail with a pre-filled message for the user.
Role-based login and navigation (Admin → Dashboard, Tutor → WelcomeTutor, Student → WelcomeStudent).
SQLite database storing all user information, roles, and registration statuses.
Logout functionality for all user roles.

A demo video showing:

Student and Tutor registration process
Admin approval and rejected request handling
Re-approval of rejected users
Email intent launching upon admin action
Role-based login and logout
SQLite data evidence
(Video attached in this release)

You can view our Deliverable 2 demo here:
https://youtu.be/qGH-HmAW7L0

UML()

Notes:

All data is stored locally using SQLite.
Admin credentials are automatically seeded at app startup.
Tested successfully on Android Studio Emulator (Pixel 6, API 34).

Bonus feature (email intent) implemented and functional.