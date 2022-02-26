**Earthquakes**
===============

### **About Project**

*	Earthquakes App displays a list of earthquakes in the world from the U.S. Geological Survey(USGS) organization.

### **Tools Used:-**

*	_Android Studio_ is the official IDE for android development. Our application was also built on the Android Studio IDE.

*	_Java_ is a very powerful OOP language, which is used for the backend development of our application.
    
*	_XML_ is also called as the Extensible Markup Language, which has been used in our application for frontend development.

### **APIs Used:-**

*	_USGS API:_ This is an implementation of the FDSN Event Web Service Specification, and allows custom searches for earthquake information using a variety of parameters. A simple call to this API can be made by: https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2022-01-01&endtime=2022-02-01&minmagnitude=3&maxmagnitude=7&limit=20&orderby=time. This GET request contains various parameters such as - Response Format, Date(From), Date(To), Minimum Magnitude, Maximum Magnitude, Total Limit and the Query of Ordering. No authentication/headers are required to use this API.

### **Project Requirements:-**

*	_Language:_ Java and XML
*	_Gradle Version:_ 7.2
*	_Gradle Plugin Version:_ 7.1.2
*	_Gradle JDK Version:_ 11.0.11
*	_Compile SDK Version:_ API-31 (Android 12.0 (S))
