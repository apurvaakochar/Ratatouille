# Ratatouille
Problem Statement: To create an Android Application for showing nearby food trucks in the city of San Francisco, CA, US.


Solution:

	• Create a Map View, where user can choose a location by specifying the address or by long pressing on the map.

	• The user can filter the results among various choices available.

	• The results will be displayed on the Map through different Markers.

	• These Markers on being clicked would display the details of the Food truck including Name, Address, Menu and Timings.


Minimum SDK: API 21

Target SDK: API 26

Tested on: API 23


Some Key APIs and features used:

	• Google Maps API v2

	• Place API

	• Database: JSON

	• Asynchronous Processing


Google Maps API was chosen as it is the quite efficient in providing the features as required by this prototype like adding markers, on click listeners, among others.


PlaceAutoCompleteFragment (part of the place API), was chosen as it does Geocoding and auto complete a user's choice. It is a simple widget to implement with much less programming lines required and is powered by Google.


As the dataset was light there was no requirement of create sqlite database in user's devices. The dataset that was available in .csv was simply converted to .json.


Calculation of distance between two coordinates, JSON Parsing and No SQL database queries were done in the back ground thread using AsyncTask to prevent the UI Thread from crashing.


Architecture:

Since the application requires very minimal functionalities, the architecture is quite simple. Everything runs in the Map Activity, both in the UI and background thread and the filters are applied through the Filter Activity.


Future Suggestions:

	• The search can be more precise.

	• There can be more categories of food.

	• There can be more identifiers for a particluar genre of food.

	• A functionality of only displaying trucks which are open now can be added.

	• For the people living in SF, an option of searching with the current location can be added.

	• Google Maps Navigation can be launched with an external intent for a particular location.

	• This did not make sense here, as it was not tested in SF.

	• But this functionality can be tested through unit tests, faking the coordinates of a location of San Francisco as current location.

	• More inputs by the user.

	• The users can have the option of updating the pictures, timings and rating the place.



Author:

Apurva Kochar

Pessl Instruments GmbH

https://www.linkedin.com/in/apurva-kochar-750a627a/

Other links:

https://github.com/apurvaakochar/MasterThesis

