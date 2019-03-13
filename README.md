# Cleanse Project

Cleanse is a Firebase-based crowdsourcing app that allows you to connect to local communities to save the environment.

## Features

### Events
Easily create new location-based events, and view them ordered by distance or date

### Chats
Create ont-to-one or group ral-time chats with push notifications

### Places
Integrated map with the locations of every event  
The events get loaded while you browse through it

## Requirements

As this is a Firebase connected app, it needs a serviceAccount.json file on the app module  
An api.xml on the modules folder is also required with the the following information

````
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="firebase_web_client_id" translatable="false">YOUR_FIREBASE_WEB_CLIENT_ID</string>
    <string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">YOUR_GOOGLE_MAPS_API_KEY</string>
</resources>
````

## Deployment

In order to deploy the app, you only need to connect it to Firebase.  
For push notifications to work, take a look at [Cleanse FCM](https://github.com/CleanseProject/CleanseFCM)
for a Google Cloud based solution designed specifically for this app
