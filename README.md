# Cleanse Project

## Requirements

As this is a Firebase connected app, it needs a serviceAccount.json file on the app module  
An api.xml on the modules folder is also required with the the following information

````
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="firebase_web_client_id" translatable="false">YOUR_FIREBASE_WEB_CLIENT_ID</string>
    <string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">YOUR_GOOGLE_MAPS_API_KEY</string>
    <string name="fabric_api_key" translatable="false">YOUR_FABRIC_API_KEY</string>
</resources>
````

## Deployment
