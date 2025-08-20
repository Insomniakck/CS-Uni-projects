## About Android Application
In this part of the project, we built a Gmail android app.
We implemented MVVM architecture, Room and Repository pattern.
Also, we adjusted our MVC server so that the data will is stored in MongoDB server instead of arrays.
The application includes the following activities:
- Login screen
- Registration screen
- Main screen

The app supports the following functionality:
- Displaying the inbox, labels, and menus
- Viewing full details of an email, composing and sending new emails, editing, deleting, etc.
- Searching emails and displaying search results

Additionally, just like in Gmail, the app allows users to star emails, move them to Trash or Spam.
A dark mode version of the application is also available, and can be activated by changing device's settings.
The application offers full support for both English and Hebrew languages.


## Getting Started
1. Sign Up
To begin, create a new account by clicking the "New to Gmail? Sign Up" link on the login page.
You will be redirected to the sign-up screen, where you can enter your personal details.

Please note the following input requirements:
- First and last names must contain only alphabetic characters.
- Date of birth must follow the format DD/MM/YYYY.
- Username must be unique.
- Password must be at least 8 characters long and include both letters and digits.

You may optionally upload a profile image from your device. If no image is selected, a default profile picture will be assigned.

2. Login
After completing the registration, you will be redirected to the login screen.
Enter your email and password to access your account.
Upon successful login, your inbox will be displayed.

3. Sending Emails
You can compose and send emails to any existing user, including yourself, by tapping the "+" button located in the bottom-right corner of the screen.

4. Viewing and Managing Emails
To view full details of an email, simply tap on it.
Within the email view, you can perform various actions:
- Star the email
- Move to Trash
- Mark as Spam
- Assign Labels

5. Searching and Filtering
Use the search bar at the top of the screen to find emails by keywords.
You can also filter emails using the side menu, which allows you to view messages by category:
- Inbox
- Sent
- Drafts
- Spam
- Labels

To create a new custom label, open the left menu and click the "+" icon next to the Labels section.


## Images
<div align="center">
  <img src="../images/android/loginpage.jpg" width="180" style="margin: 8px"/>
  <img src="../images/android/signup.jpg" width="180" style="margin: 8px"/>
  <img src="../images/android/enterbirthdate.jpg" width="180" style="margin: 8px"/>
  <img src="../images/android/chooseprofile.jpg" width="180" style="margin: 8px"/>
</div>

<div align="center">
  <img src="../images/android/login.jpg" width="180" style="margin: 8px"/>
  <img src="../images/android/inboxen.jpg" width="180" style="margin: 8px"/>
  <img src="../images/android/leftmenu.jpg" width="180" style="margin: 8px"/>
  <img src="../images/android/createmail.jpg" width="180" style="margin: 8px"/>
</div>

<div align="center">
  <img src="../images/android/createlabel.jpg" width="180" style="margin: 8px"/>
  <img src="../images/android/singlemail.jpg" width="180" style="margin: 8px"/>
  <img src="../images/android/labelmail.jpg" width="180" style="margin: 8px"/>
  <img src="../images/android/labelsmenu.jpg" width="180" style="margin: 8px"/>
</div>

<div align="center">
  <img src="../images/android/darkmode.jpg" width="180" style="margin: 8px"/>
  <img src="../images/android/rightmenuhe.jpg" width="180" style="margin: 8px"/>
  <img src="../images/android/inboxhe.jpg" width="180" style="margin: 8px"/>
  <img src="../images/android/singlemailhe.jpg" width="180" style="margin: 8px"/>
</div>