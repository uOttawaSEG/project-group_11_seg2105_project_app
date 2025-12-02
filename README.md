Group 11
Team: Mamadou Diallo, Mahdi Hassoun, Atique Aqtab, Kashif Karim, Zachary Yassine, Eren Arikan

Accounts for Testing

Administrator:
Email: admin@uottawa.ca
Password: admin123

Sample Tutor:
Email: tutor@uottawa.ca
Password: teach123

Sample Student:
Email: student@uottawa.ca
Password: pass123

How to Run
Install the Project_Group_11_debug.apk on an Android device or emulator (Android 10+).
Launch the app.
Sign in using one of the accounts above or register a new Student or Tutor account.
The app stores data in SQLite on the device.
The Administrator account loads automatically at first launch.

Implemented Features
Registration and Login
Student and Tutor registration forms with input validation.
Every new Student or Tutor enters a pending state after registration.

Login reflects registration status:
Approved: user reaches their dashboard.
Pending: user receives a pending message.
Rejected: user receives a rejection message and contact notice.
Administrator Dashboard
Inbox displays all pending registrations.
Admin views all submitted user info (no passwords).
Admin approves or rejects requests.
A Rejected Requests screen displays previously rejected users.
Admin re-approves rejected users.

Role Navigation
Admin → Admin Dashboard
Tutor → Tutor Welcome Screen
Student → Student Welcome Screen
Logout available for all roles.

Bonus Feature
When the Admin approves or rejects a request, Gmail opens with a pre-filled message addressed to the user.
Database

SQLite stores:
Users
Roles
Registration status (Pending, Approved, Rejected)

Testing
Successful tests in Android Studio Emulator (Pixel 6, API 34).
APK runs correctly on physical Android devices.

Demo Video:
https://youtu.be/zZ_mBmMxc2c

UML:
https://github.com/uOttawaSEG/project-group_11_seg2105_project_app/blob/main/UML%20Diagram%20-%20Deliverable%20%233.pdf