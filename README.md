## Sleep Track +

“SleepTrack +” is an application designed to enter information around your waking and standing heart rate, and the quality and amount of sleep you have had the past night. Included is the opportunity to enter a memorable dream. The ‘+’ version of the application is the diary entry service, where users are recommended to keep a diary of their day.


- Dashboard:

The app starts at a dashboard, where empty graphs and a counter are shown, where the graphs display the users heart rates and sleep data once filled in. These graphs display a week’s amount of data at a time and automatically update on each new entry. 


- Morning Monitoring Form:

The next section is morning monitoring, where the user can enter their monitoring data including waking and standing heart rates, mental state, sleep quality and any memorable dreams they may have. Upon submission the user is redirected back to the Dashboard where the graphs will populate with data. 


- Diary Entry:

The diary fragment is a List View displaying all current entries within the system. An example diary is already available within the list explaining how the diary section works. Clicking on any entry will bring the entry onto the screen. To add a new entry, a floating action button is provided that once pressed allows the user to enter a title and the diary entry. 


- Options Menu:

Within the options menu, the user is able to provide their name and take a profile picture to display in the navigation drawer of the application. Furthermore, buttons to reset the username and profile picture diary entries and monitoring entries are used to delete all entries in the application. Doing this is unreversible and all data will be lost.

When navigating to the Options menu, the user is prompted with the option to allow or deny read access to photos, media and files on the device as well as access to use the camera. This is to allow a profile picture to be taken.


- Known Bugs:

•	When pressing the ‘Take a Profile Picture’ button, on the first installation of the app, the camera access dialog appears and when allowed, the user is returned to the options menu. Pressing the button again launches the camera intent. 

•	Pressing the back button on the dashboard displays a blank page before the app closes.

•	On API 27, when first launching the camera, the app doesn’t redirect back to the app. Closing the camera through the back button and repressing the ‘Take a Profile Picture’ works as intended.

